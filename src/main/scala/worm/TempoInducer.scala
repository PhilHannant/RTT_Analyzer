/*  Performance Worm: Visualisation of Expressive Musical Performance
	Copyright (C) 2001, 2006 by Simon Dixon

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License along
	with this program (the file gpl.txt); if not, download it from
	http://www.gnu.org/licenses/gpl.txt or write to the
	Free Software Foundation, Inc.,
	51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package worm

import java.io.{File, FileWriter, IOException}

import at.ofai.music.util.Format

object TempoInducer {
  private[worm] val MEMORY: Double = 8.0
  private[worm] val MIN_IOI: Double = 0.1
  private[worm] val MAX_IOI: Double = 2.5
  private[worm] val MIN_IBI: Double = 0.200
  private[worm] val LOW_IBI: Double = 0.400
  private[worm] val DEF_IBI: Double = 0.800
  private[worm] val HI_IBI: Double = 1.200
  private[worm] val MAX_IBI: Double = 1.500
  private[worm] val HYP_CHANGE_FACTOR: Double = (1 - 0.4)
  private[worm] val AMP_MEM_FACTOR: Double = 0.95
  private[worm] val DECAY_BEST: Double = 0.9
  private[worm] val DECAY_OTHER: Double = 0.8
  private[worm] val RATIO_ERROR: Double = 0.1
  private[worm] val CLUSTER_WIDTH: Int = 8
  private[worm] val CLUSTER_FACTOR: Int = 30
  private[worm] val CLUSTER_POINTS: Int = 10
  private[worm] val REGRESSION_SIZE: Int = 4
  private[worm] val SLOPE_POINTS: Int = 15
  private[worm] val MID_POINT: Int = SLOPE_POINTS / 2
  private[worm] val PEAK_POINTS: Int = MEMORY.toInt * 20
  private[worm] val OVERLAP: Int = 4
  private[worm] var plot: Plot = null
  private[worm] val plotFlag: Boolean = false

  private[worm] def next(p: Int): Int = {
    return if ((p == PEAK_POINTS - 1)) 0
    else p + 1
  }

  private[worm] def prev(p: Int): Int = {
    return if ((p == 0)) PEAK_POINTS - 1
    else p - 1
  }

  private[worm] def top(p: Int): Int = {
    return p + p / CLUSTER_FACTOR + CLUSTER_WIDTH
  }
}

class TempoInducer(var timeBase: Double) {
  ioiPoints = Math.ceil(TempoInducer.MAX_IOI / timeBase).toInt + 1
  envelope = new Array[Double](TempoInducer.SLOPE_POINTS)
  slope = new Array[Double](TempoInducer.SLOPE_POINTS)
  peakTime = new Array[Double](TempoInducer.PEAK_POINTS)
  peakSPL = new Array[Double](TempoInducer.PEAK_POINTS)
  iois = new Array[Double](ioiPoints)
  cluster = new Array[Double](TempoInducer.CLUSTER_POINTS)
  clusterWgt = new Array[Double](TempoInducer.CLUSTER_POINTS)
  newCluster = new Array[Double](TempoInducer.CLUSTER_POINTS)
  newClusterWgt = new Array[Double](TempoInducer.CLUSTER_POINTS)
  best = new Array[Double](TempoInducer.CLUSTER_POINTS)
  bestWgt = new Array[Double](TempoInducer.CLUSTER_POINTS)
  bestUsed = new Array[Boolean](TempoInducer.CLUSTER_POINTS)
  bestCount = 0
  peakHead = 0
  peakTail = TempoInducer.PEAK_POINTS - 1
  tempo = TempoInducer.DEF_IBI
  counter = 0
  longTermSum = 0
  recentSum = 0
  prevAmp = new Array[Double](TempoInducer.OVERLAP)
  xplot = new Array[Double](ioiPoints)
  var i: Int = 0
  while (i < ioiPoints) {
    xplot(i) = timeBase * i
    ({
      i += 1; i - 1
    })
  }
  yplot = new Array[Double](ioiPoints)
  xplot2 = new Array[Double](TempoInducer.CLUSTER_POINTS)
  yplot2 = new Array[Double](TempoInducer.CLUSTER_POINTS)
  xplot3 = new Array[Double](TempoInducer.CLUSTER_POINTS)
  yplot3 = new Array[Double](TempoInducer.CLUSTER_POINTS)
  if (TempoInducer.plotFlag) makePlot
  private[worm] var envelope: Array[Double] = null
  private[worm] var slope: Array[Double] = null
  private[worm] var peakTime: Array[Double] = null
  private[worm] var peakSPL: Array[Double] = null
  private[worm] var iois: Array[Double] = null
  private[worm] var cluster: Array[Double] = null
  private[worm] var clusterWgt: Array[Double] = null
  private[worm] var newCluster: Array[Double] = null
  private[worm] var newClusterWgt: Array[Double] = null
  private[worm] var best: Array[Double] = null
  private[worm] var bestWgt: Array[Double] = null
  private[worm] var bestUsed: Array[Boolean] = null
  private[worm] var peakHead: Int = 0
  private[worm] var peakTail: Int = 0
  private[worm] var bestCount: Int = 0
  private[worm] var tempo: Double = .0
  private[worm] var counter: Int = 0
  private[worm] var longTermSum: Double = .0
  private[worm] var recentSum: Double = .0
  private[worm] var prevAmp: Array[Double] = null
  private[worm] var ioiPoints: Int = 0
  var onset: Boolean = false
  private[worm] var xplot: Array[Double] = null
  private[worm] var yplot: Array[Double] = null
  private[worm] var xplot2: Array[Double] = null
  private[worm] var yplot2: Array[Double] = null
  private[worm] var xplot3: Array[Double] = null
  private[worm] var yplot3: Array[Double] = null

  private[worm] def makePlot {
    System.out.println("makePlot() " + (TempoInducer.plot == null))
    if (TempoInducer.plot == null) TempoInducer.plot = new Plot
    else TempoInducer.plot.clear
    TempoInducer.plot.addPlot(xplot, yplot, java.awt.Color.blue, PlotPanel.IMPULSE)
    TempoInducer.plot.addPlot(xplot2, yplot2, java.awt.Color.green)
    TempoInducer.plot.addPlot(xplot3, yplot3, java.awt.Color.red)
    TempoInducer.plot.setTitle("Tempo Tracking Histogram and Clusters")
  }

  def getTempo(amp: Double): Double = {
    System.out.println("ampl " + amp)
    var i: Int = 0
    counter += 1
    i = 0
    while (i < TempoInducer.SLOPE_POINTS - 1) {
      {
        envelope(i) = envelope(i + 1)
        slope(i) = slope(i + 1)
      }
      ({
        i += 1; i - 1
      })
    }
    envelope(i) = amp
    var j: Int = 0
    while (j < TempoInducer.OVERLAP) {
      {
        envelope(i) += prevAmp(j)
        if (j == TempoInducer.OVERLAP - 1) prevAmp(j) = amp
        else prevAmp(j) = prevAmp(j + 1)
      }
      ({
        j += 1; j - 1
      })
    }
    longTermSum += amp
    if (recentSum == 0) recentSum = amp
    else recentSum = TempoInducer.AMP_MEM_FACTOR * recentSum + (1 - TempoInducer.AMP_MEM_FACTOR) * amp
    slope(i) = getSlope
    i = 0
    while (i < TempoInducer.SLOPE_POINTS) {
      if ((i != TempoInducer.MID_POINT) && (slope(TempoInducer.MID_POINT) <= slope(i))) break //todo: break is not supported
      ({
        i += 1; i - 1
      })
    }
    if ((i == TempoInducer.SLOPE_POINTS) && (envelope(TempoInducer.MID_POINT) > recentSum / 5) && (slope(TempoInducer.MID_POINT) > recentSum / (2 * TempoInducer.REGRESSION_SIZE * timeBase))) {
      addPeak(timeBase * (counter - TempoInducer.MID_POINT), envelope(TempoInducer.MID_POINT))
    }
    else onset = false
    System.out.println("tempo " + 60 / tempo)
    return tempo
  }

  def switchLevels(faster: Boolean) {
    var i: Int = 1
    while (i < bestCount) {
      if ((best(i) < best(0)) == faster) {
        val tmp: Double = best(0)
        best(0) = best(i)
        best(i) = tmp
        break //todo: break is not supported
      }
      ({
        i += 1; i - 1
      })
    }
  }

  private[worm] def addPeak(t: Double, dB: Double) {
    java.util.Arrays.fill(iois, 0)
    peakTime(peakHead) = t
    peakSPL(peakHead) = dB
    peakHead = TempoInducer.next(peakHead)
    if (peakHead == peakTail) System.err.println("Overflow: too many peaks")
    var loPtr: Int = TempoInducer.next(peakTail)
    while (t - peakTime(loPtr) > TempoInducer.MEMORY) {
      {
        peakTail = loPtr
        loPtr = TempoInducer.next(peakTail)
      }
    }
    var hiPtr: Int = 0

    while (loPtr != peakHead) {
      hiPtr = TempoInducer.next(loPtr)
      while (hiPtr != peakHead) {
        {
          val ioi: Double = peakTime(hiPtr) - peakTime(loPtr)
          if (ioi >= TempoInducer.MAX_IOI) break //todo: break is not supported
          iois(Math.rint(ioi / timeBase).toInt) += Math.sqrt(peakSPL(hiPtr) * peakSPL(loPtr))
        }
        hiPtr = TempoInducer.next(hiPtr)
      }
      loPtr = TempoInducer.next(loPtr)
    }
    var i: Int = 0
    while (i < iois.length) {
      yplot(i) = iois(i)
      ({
        i += 1; i - 1
      })
    }
    var clusterCount: Int = 0

    while (clusterCount < TempoInducer.CLUSTER_POINTS) {
      {
        var sum: Double = 0
        var max: Double = 0
        var maxIndex: Int = 0
        hiPtr = (TempoInducer.MIN_IOI / timeBase).toInt
        loPtr = hiPtr
        while (hiPtr < ioiPoints) {
          {
            if (hiPtr >= TempoInducer.top(loPtr)) sum -= iois(({
              loPtr += 1; loPtr - 1
            }))
            else {
              sum += iois(({
                hiPtr += 1; hiPtr - 1
              }))
              if (sum / (TempoInducer.top(loPtr) - loPtr) > max) {
                max = sum / (TempoInducer.top(loPtr) - loPtr)
                maxIndex = loPtr
              }
            }
          }
        }
        if (max == 0) break //todo: break is not supported
        hiPtr = TempoInducer.top(maxIndex)
        if (hiPtr > ioiPoints) hiPtr = ioiPoints
        sum = 0
        var weights: Double = 0
        loPtr = maxIndex
        while (loPtr < hiPtr) {
          {
            sum += loPtr * iois(loPtr)
            weights += iois(loPtr)
            iois(loPtr) = 0
          }
          ({
            loPtr += 1; loPtr - 1
          })
        }
        cluster(clusterCount) = sum / weights * timeBase
        clusterWgt(clusterCount) = max
      }
      ({
        clusterCount += 1; clusterCount - 1
      })
    }
    var i: Int = 0
    while (i < clusterCount) {
      {
        newCluster(i) = cluster(i) * clusterWgt(i)
        newClusterWgt(i) = clusterWgt(i)
        var j: Int = 0
        while (j < clusterCount) {
          {
            if (i != j) {
              val ratio: Int = getRatio(cluster(i), cluster(j))
              if (ratio > 0) {
                newCluster(i) += cluster(j) * clusterWgt(j)
                newClusterWgt(i) += clusterWgt(j) / ratio
              }
              else if (ratio < 0) {
                newCluster(i) += cluster(j) * clusterWgt(j) / (ratio * ratio)
                newClusterWgt(i) += clusterWgt(j) / -ratio
              }
            }
          }
          ({
            j += 1; j - 1
          })
        }
        newCluster(i) /= newClusterWgt(i)
      }
      ({
        i += 1; i - 1
      })
    }
    var i: Int = 0
    while (i < TempoInducer.CLUSTER_POINTS) {
      {
        if (i < clusterCount) {
          xplot2(i) = cluster(i)
          yplot2(i) = clusterWgt(i) * 3
          xplot3(i) = newCluster(i)
          yplot3(i) = newClusterWgt(i) * 1.5
        }
        else xplot2(i) = yplot2(i) = xplot3(i) = yplot3(i) = 0
      }
      ({
        i += 1; i - 1
      })
    }
    if (TempoInducer.plotFlag) {
      if (TempoInducer.plot == null) makePlot
      TempoInducer.plot.update
    }
    else if (TempoInducer.plot != null) {
      TempoInducer.plot.close
      TempoInducer.plot = null
    }
    val dt: Double = t - peakTime(TempoInducer.prev(TempoInducer.prev(peakHead)))
    var i: Int = 0
    while (i < bestCount) {
      {
        bestUsed(i) = false
        if (i != 0) bestWgt(i) *= Math.pow(TempoInducer.DECAY_OTHER, dt)
        else bestWgt(i) *= Math.pow(TempoInducer.DECAY_BEST, dt)
        if (best(i) < TempoInducer.LOW_IBI) bestWgt(i) *= 1 - Math.pow((TempoInducer.LOW_IBI - best(i)) / (2 * (TempoInducer.LOW_IBI - TempoInducer.MIN_IBI)), 3)
        else if (best(i) > TempoInducer.HI_IBI) bestWgt(i) *= 1 - Math.pow((best(i) - TempoInducer.HI_IBI) / (2 * (TempoInducer.MAX_IBI - TempoInducer.HI_IBI)), 3)
      }
      ({
        i += 1; i - 1
      })
    }
    var i: Int = 0
    while (i < clusterCount) {
      {
        if ((newCluster(i) < TempoInducer.MIN_IBI) || (newCluster(i) > TempoInducer.MAX_IBI)) continue //todo: continue is not supported
      val dMax: Double = newCluster(i) / TempoInducer.CLUSTER_FACTOR + TempoInducer.CLUSTER_WIDTH * timeBase
        var dMin: Double = dMax / 2
        var index: Int = -1
        var j: Int = 0
        while (j < bestCount) {
          {
            val diff: Double = Math.abs(newCluster(i) - best(j))
            if (diff < dMin) {
              dMin = diff
              index = j
            }
          }
          ({
            j += 1; j - 1
          })
        }
        if (index >= 0) {
          if (bestUsed(index)) continue //todo: continue is not supported
          best(index) += (newCluster(i) - best(index)) * TempoInducer.HYP_CHANGE_FACTOR
          bestWgt(index) += newClusterWgt(i) * (1 - dMin / dMax)
        }
        else if (bestCount < TempoInducer.CLUSTER_POINTS) {
          best(bestCount) = newCluster(i)
          bestWgt(({
            bestCount += 1; bestCount - 1
          })) = newClusterWgt(i)
        }
        else if (bestWgt(bestCount - 1) < newClusterWgt(i)) {
          best(bestCount - 1) = newCluster(i)
          bestWgt(bestCount - 1) = newClusterWgt(i)
        }
      }
      ({
        i += 1; i - 1
      })
    }
    var i: Int = 0
    while (i < bestCount) {
      var j: Int = i + 1
      while (j < bestCount) {
        if (Math.abs(best(i) - best(j)) < TempoInducer.CLUSTER_WIDTH * timeBase / 2) {
          best(i) = (best(i) * bestWgt(i) + best(j) * bestWgt(j)) / (bestWgt(i) + bestWgt(j))
          bestWgt(i) += bestWgt(j)
          var k: Int = j + 1
          while (k < bestCount) {
            {
              best(k - 1) = best(k)
              bestWgt(k - 1) = bestWgt(k)
            }
            ({
              k += 1; k - 1
            })
          }
          bestCount -= 1
          j -= 1
        }
        ({
          j += 1; j - 1
        })
      }
      ({
        i += 1; i - 1
      })
    }
    var change: Boolean = true
    while (change) {
      {
        change = false
        var i: Int = bestCount - 1
        while (i > 0) {
          if (bestWgt(i) > bestWgt(i - 1)) {
            change = true
            var tmp: Double = bestWgt(i)
            bestWgt(i) = bestWgt(i - 1)
            bestWgt(i - 1) = tmp
            tmp = best(i)
            best(i) = best(i - 1)
            best(i - 1) = tmp
          }
          ({
            i -= 1; i + 1
          })
        }
      }
    }
    if (bestCount > 0) {
      tempo = best(0)
    }
  }

  private[worm] def showTime {
    System.out.println("Time = " + Format.d(timeBase * (counter - TempoInducer.MID_POINT), 3) + "\n")
  }

  private[worm] def saveHist {
    try {
      Format.init(1, 1, 3, false)
      val outfile: FileWriter = new FileWriter(new File("worm-dance.tmp"))
      var i: Int = 0
      while (i < TempoInducer.CLUSTER_POINTS) {
        outfile.write((if ((i < bestCount)) (Format.d(best(i), 3) + "   " + Format.d(bestWgt(i), 3))
        else "") + "\n")
        ({
          i += 1; i - 1
        })
      }
      outfile.write("\n")
      outfile.close
      System.exit(0)
    }
    catch {
      case e: IOException => {
        System.err.println("Exception in saveHist(): " + e)
      }
    }
  }

  private[worm] def getRatio(a: Double, b: Double): Int = {
    var r: Double = a / b
    if (r < 1) r = -1 / r
    val round: Int = Math.rint(r).toInt
    if ((Math.abs((r - round) / r) < TempoInducer.RATIO_ERROR) && (Math.abs(round) >= 2) && (Math.abs(round) <= 8)) return round
    return 0
  }

  private[worm] def getSlope: Double = {
    val start: Int = TempoInducer.SLOPE_POINTS - TempoInducer.REGRESSION_SIZE
    var sx: Double = 0
    var sxx: Double = 0
    var sy: Double = 0
    var sxy: Double = 0
    var i: Int = 0
    while (i < TempoInducer.REGRESSION_SIZE) {
      {
        sx += i
        sxx += i * i
        sy += envelope(start + i)
        sxy += i * envelope(start + i)
      }
      ({
        i += 1; i - 1
      })
    }
    return (4 * sxy - sx * sy) / (4 * sxx - sx * sx) / timeBase
  }
}