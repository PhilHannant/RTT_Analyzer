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

import java.io.{File, FileNotFoundException, IOException}
import java.net.URL
import javax.sound.sampled._

import liveaudio.SoundCaptureImpl

object AudioWorm {
  private[worm] val defaultSampleRate: Float = 44100
  private[worm] val windowTime: Double = 0.010
  private[worm] val averageCount: Int = 10
  private[worm] val fileDelay: Int = 180

  def checkAudioFormats(out: AudioFormat, in: AudioFormat): Double = {
    if ((out.getChannels != in.getChannels) || (out.getSampleSizeInBits != in.getSampleSizeInBits) || (out.getEncoding ne in.getEncoding) || (out.isBigEndian != in.isBigEndian) || (out.getSampleRate == 0.0F)) return 2.0
    if (out.getSampleRate < 0) return -1.0
    return Math.abs(2.0 * (out.getSampleRate - in.getSampleRate) / (out.getSampleRate + in.getSampleRate))
  }

  def rms(data: Array[Double]): Double = {
    var sum: Double = 0
    var i: Int = 0
    while (i < data.length) {
      sum += data(i) * data(i)
      ({
        i += 1; i - 1
      })
    }
    return Math.sqrt(sum / data.length.toDouble)
  }
}

class AudioWorm {
  jumpPosition = -1
  targetDataLine = null
  ti = new TempoInducer(AudioWorm.windowTime)
  audioFile = null
  audioURL = null
  if (audioFileName == null) audioFileName = ""
  if (audioFilePath == null) audioFilePath = ""
  isFileInput = (!audioFileName == "")
  if (!isFileInput) {
    initSoundCardInput
    return
  }
  if (audioFileName.startsWith("http:")) try {
    audioURL = new URL(audioFileName)
  }
  catch {
    case e: MalformedURLException => {
      e.printStackTrace
    }
  }
  else {
    audioFile = new File(audioFileName)
    if (!audioFile.isFile) audioFile = new File(audioFilePath + audioFileName)
    if (!audioFile.isFile) audioFile = new File("//fichte" + audioFileName)
    if (!audioFile.isFile) audioFile = new File("//fichte" + audioFilePath + audioFileName)
  }
  resetAudioFile
  if ((wormData != null) && (wormData.time(0) > 0.5)) skipTo(wormData.time(0) - 0.5)
  private[worm] val gui: Worm = null
  private[worm] var in: AudioInputStream = null
  private[worm] var orig: AudioInputStream = null
  private[worm] var isConverting: Boolean = false
  private[worm] var targetDataLine: TargetDataLine = null
  private[worm] var isFileInput: Boolean = false
  private[worm] var inputFormat: AudioFormat = null
  private[worm] var out: SourceDataLine = null
  private[worm] var outputFormat: AudioFormat = null
  private[worm] var outputBufferSize: Int = 0
  private[worm] var frameSize: Int = 0
  private[worm] var frameRate: Double = .0
  private[worm] var channels: Int = 0
  private[worm] var sampleSizeInBytes: Int = 0
  private[worm] var windowSize: Int = 0
  private[worm] var normalise: Double = .0
  private[worm] var inputBuffer: Array[Byte] = null
  private[worm] var bytesRead: Int = 0
  private[worm] var blockCount: Int = 0
  private[worm] var ti: TempoInducer = null
  private[worm] val wormData: WormFile = null
  private[worm] var audioFileName: String = null
  private[worm] var audioFilePath: String = null
  private[worm] var audioFile: File = null
  private[worm] var audioURL: URL = null
  private[worm] var bytePosition: Long = 0L
  private[worm] var jumpPosition: Long = 0L
  private[worm] var fileLength: Long = 0L
  private[worm] var count: Int = 0
  private[worm] val sc: SoundCaptureImpl = null

  protected def resetAudioFile {
    try {
      isConverting = false
      orig = null
      if (audioFile == null) in = AudioSystem.getAudioInputStream(audioURL)
      else {
        if (!audioFile.isFile) throw (new FileNotFoundException("No file: " + audioFileName))
        in = AudioSystem.getAudioInputStream(audioFile)
      }
      inputFormat = in.getFormat
      if (inputFormat.getEncoding ne AudioFormat.Encoding.PCM_SIGNED) {
        val desiredFormat: AudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, inputFormat.getSampleRate, 16, inputFormat.getChannels, inputFormat.getChannels * 2, inputFormat.getSampleRate, false)
        orig = in
        in = AudioSystem.getAudioInputStream(desiredFormat, orig)
        inputFormat = in.getFormat
        isConverting = true
      }
      fileLength = in.available
      bytePosition = 0
      bytesRead = 0
      blockCount = 0
    }
    catch {
      case e: IOException => {
        e.printStackTrace
        in = null
      }
      case e: IllegalArgumentException => {
        e.printStackTrace
        in = null
      }
      case e: UnsupportedAudioFileException => {
        e.printStackTrace
        in = null
      }
    }
  }

  protected def initSoundCardInput {
    if (in != null) {
      try {
        in.close
      }
      catch {
        case e: Exception => {
        }
      }
      in = null
    }
    if (targetDataLine != null) {
      try {
        targetDataLine.close
      }
      catch {
        case e: Exception => {
        }
      }
      targetDataLine = null
    }
    inputFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AudioWorm.defaultSampleRate, 16, 2, 4, AudioWorm.defaultSampleRate, false)
    val mInfo: Array[Mixer.Info] = AudioSystem.getMixerInfo
    System.out.println("Number of mixers: " + mInfo.length)
    var i: Int = 0
    while (i < mInfo.length) {
      {
        System.out.println("Mixer info : " + mInfo(i))
        val t: Mixer = AudioSystem.getMixer(mInfo(i))
        val li: Array[Line.Info] = t.getTargetLineInfo
        var c: Class[_] = null
        System.out.println("Number of target lines: " + li.length)
        var j: Int = 0
        while (j < li.length) {
          {
            System.out.println("Line info: " + li(j))
            c = li(j).getLineClass
            val af: Array[AudioFormat] = (li(j).asInstanceOf[DataLine.Info]).getFormats
            var k: Int = 0
            while (k < af.length) {
              {
                val err: Double = AudioWorm.checkAudioFormats(af(k), inputFormat)
                if (err < 0.01) {
                  if (err >= 0) inputFormat = af(k)
                  val info: DataLine.Info = new DataLine.Info(c, inputFormat)
                  try {
                    System.out.println("Getting line with " + info)
                    if (AudioSystem.getLine(info).isInstanceOf[TargetDataLine]) {
                      targetDataLine = null
                      targetDataLine = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
                      System.out.println("Opening line ... ")
                      System.out.println("framerate " + inputFormat.getFrameRate)
                      System.out.println("framesize " + inputFormat.getFrameSize)
                      targetDataLine.open(inputFormat)
                      System.out.println("Creating AudioInputStream")
                      in = new AudioInputStream(targetDataLine)
                      init
                      return
                    }
                  }
                  catch {
                    case e: Exception => {
                      System.err.println("Unable to open input line")
                      e.printStackTrace
                      System.exit(1)
                    }
                  }
                }
              }
              ({
                k += 1; k - 1
              })
            }
          }
          ({
            j += 1; j - 1
          })
        }
      }
      ({
        i += 1; i - 1
      })
    }
    throw new RuntimeException("No suitable input line found")
  }

  protected def init {
    if (out != null) out.close
    try {
      if (inputFormat.getEncoding ne AudioFormat.Encoding.PCM_SIGNED) throw new UnsupportedAudioFileException("Not PCM_SIGNED but " + inputFormat.getEncoding)
      frameSize = inputFormat.getFrameSize
      frameRate = inputFormat.getFrameRate
      channels = inputFormat.getChannels
      sampleSizeInBytes = frameSize / channels
      windowSize = (AudioWorm.windowTime * frameRate).toInt
      normalise = channels.toDouble * windowSize * (1 << (inputFormat.getSampleSizeInBits - 1))
      inputBuffer = new Array[Byte](windowSize * frameSize)
      bytePosition = 0
      bytesRead = 0
      blockCount = 0
      if (!isFileInput) return
      val mInfo: Array[Mixer.Info] = AudioSystem.getMixerInfo
      var i: Int = 0
      while (i < mInfo.length) {
        {
          val t: Mixer = AudioSystem.getMixer(mInfo(i))
          val li: Array[Line.Info] = t.getSourceLineInfo
          var c: Class[_] = null
          var j: Int = 0
          while (j < li.length) {
            {
              c = li(j).getLineClass
              val af: Array[AudioFormat] = (li(j).asInstanceOf[DataLine.Info]).getFormats
              var k: Int = 0
              while (k < af.length) {
                {
                  val err: Double = AudioWorm.checkAudioFormats(af(k), inputFormat)
                  if (err < 0.01) {
                    if (err < 0) outputFormat = inputFormat
                    else outputFormat = af(k)
                    outputBufferSize = outputFormat.getFrameRate.toInt * frameSize * 1
                    val info: DataLine.Info = new DataLine.Info(c, outputFormat, outputBufferSize)
                    if (AudioSystem.getLine(info).isInstanceOf[SourceDataLine]) {
                      out = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
                      out.open
                      return
                    }
                  }
                }
                ({
                  k += 1; k - 1
                })
              }
            }
            ({
              j += 1; j - 1
            })
          }
        }
        ({
          i += 1; i - 1
        })
      }
      throw new LineUnavailableException("Unable to find output device" + " matching:\n\t" + inputFormat)
    }
    catch {
      case e: LineUnavailableException => {
        e.printStackTrace
        System.exit(1)
      }
      case e: UnsupportedAudioFileException => {
        e.printStackTrace
        System.exit(1)
      }
    }
  }

  def start {
    System.out.println("Start called")
    if (isFileInput) {
      out.start
    }
    else {
      System.out.println("Flushing targetDataLine")
      targetDataLine.flush
      System.out.println("Restarting targetDataLine")
      targetDataLine.start
    }
  }

  def pause {
    if (isFileInput) out.stop
    else targetDataLine.stop
  }

  def stop {
    if (isFileInput) {
      out.stop
      out.flush
    }
    else {
      targetDataLine.stop
      targetDataLine.flush
    }
  }

  @throws[IOException]
  def nextBlock: Boolean = {
    var rms: Double = 0
    var tempo: Double = 0
    var i: Int = 0
    while (i < AudioWorm.averageCount) {
      {
        val waitCount: Int = 1
        while ((in.available < inputBuffer.length) && !isFileInput) {
          {
            try {
              val before: Int = in.available
              Thread.sleep((1000.0 * AudioWorm.windowTime).toInt)
              val after: Int = in.available
              if ((waitCount > 5) && (before == after)) {
                break //todo: break is not supported
              }
              if ((waitCount > 3) && (!targetDataLine.isActive || !targetDataLine.isRunning)) return false
            }
            catch {
              case e: InterruptedException => {
              }
            }
          }
        }
        val avail: Long = (if (isConverting) orig.available
        else in.available)
        if (avail >= inputBuffer.length) {
          val tmp: Double = processWindow
          if (bytesRead < 0) {
            System.err.println("nextBlock(): Audio read error")
            return false
          }
          rms += tmp * tmp
          if (wormData == null) tempo = ti.getTempo(tmp)
          if (isFileInput) {
            if (ti.onset) var j: Int = 0
            while (j < 882) {
              {
                inputBuffer(({
                  j += 1; j - 1
                })) = (100.0 * Math.sin(2.0 * Math.PI * j / 441.0)).toByte
                inputBuffer(({
                  j += 1; j - 1
                })) = 0
              }
            }
            val chk: Int = out.write(inputBuffer, 0, bytesRead)
          }
          blockCount += 1
        }
        else {
          System.err.println("nextBlock(): Audio data not available")
          return false
        }
      }
      ({
        i += 1; i - 1
      })
    }
    val dB: Double = Math.max(0, 120 + 20 / Math.log(10) * Math.log(Math.sqrt(rms / AudioWorm.averageCount)))
    val index: Int = (blockCount - 1) / AudioWorm.averageCount
    if (wormData != null) {
      if (index >= wormData.outTempo.length) {
        return false
      }
    }
    else {
    }
    if (!isFileInput) {
      return true
    }
    val space: Int = out.available
    val buffContents: Double = (outputBufferSize - space).toDouble * AudioWorm.windowTime / inputBuffer.length
    if (buffContents > 0.1) {
      try {
        Thread.sleep(75)
      }
      catch {
        case e: InterruptedException => {
        }
      }
    }
    return true
  }

  @throws[IOException]
  protected def processWindow: Double = {
    if (jumpPosition >= 0) skipAudio
    count += 1
    System.out.println("Window " + count)
    bytesRead = in.read(inputBuffer)
    bytePosition += bytesRead
    System.out.println("total bytes " + bytePosition)
    if (wormData != null) return 0
    var sample: Long = 0L
    var sum: Double = 0
    if (sampleSizeInBytes == 1) {
      if (channels == 1) {
        var i: Int = 0
        while (i < bytesRead) {
          {
            sample = (inputBuffer(i).toInt)
            sum += (sample * sample).toDouble
          }
          i += frameSize
        }
      }
      else if (channels == 2) {
        var i: Int = 0
        while (i < bytesRead) {
          {
            sample = (inputBuffer(i).toInt) + (inputBuffer(i + 1).toInt)
            sum += (sample * sample).toDouble
          }
          i += frameSize
        }
      }
      else {
        var i: Int = 0
        while (i < bytesRead) {
          {
            sample = 0
            var c: Int = 0
            while (c < channels) {
              sample += (inputBuffer(i).toInt)
              ({
                c += 1; c - 1
              })
              ({
                i += 1; i - 1
              })
            }
            sum += (sample * sample).toDouble
          }
        }
      }
    }
    else if (sampleSizeInBytes == 2) {
      if (inputFormat.isBigEndian) {
        if (channels == 1) {
          var i: Int = 0
          while (i < bytesRead) {
            {
              sample = (inputBuffer(i).toInt << 8) | (inputBuffer(i + 1).toInt & 0xFF)
              sum += (sample * sample).toDouble
            }
            i += frameSize
          }
        }
        else if (channels == 2) {
          var i: Int = 0
          while (i < bytesRead) {
            {
              sample = ((inputBuffer(i).toInt << 8) | (inputBuffer(i + 1).toInt & 0xFF)) + ((inputBuffer(i + 2).toInt << 8) | (inputBuffer(i + 3).toInt & 0xFF))
              sum += (sample * sample).toDouble
            }
            i += frameSize
          }
        }
        else {
          var i: Int = 0
          while (i < bytesRead) {
            {
              sample = 0
              var c: Int = 0
              while (c < channels) {
                {
                  sample += (inputBuffer(i).toInt << 8) | (inputBuffer(i + 1).toInt & 0xFF)
                  i += 2
                }
                ({
                  c += 1; c - 1
                })
              }
              sum += (sample * sample).toDouble
            }
          }
        }
      }
      else {
        if (channels == 1) {
          var i: Int = 0
          while (i < bytesRead) {
            {
              sample = (inputBuffer(i + 1).toInt << 8) | (inputBuffer(i).toInt & 0xFF)
              sum += (sample * sample).toDouble
            }
            i += frameSize
          }
        }
        else if (channels == 2) {
          var i: Int = 0
          while (i < bytesRead) {
            {
              sample = ((inputBuffer(i + 1).toInt << 8) | (inputBuffer(i).toInt & 0xFF)) + ((inputBuffer(i + 3).toInt << 8) | (inputBuffer(i + 2).toInt & 0xFF))
              sum += (sample * sample).toDouble
            }
            i += frameSize
          }
        }
        else {
          var i: Int = 0
          while (i < bytesRead) {
            {
              sample = 0
              var c: Int = 0
              while (c < channels) {
                {
                  sample += (inputBuffer(i + 1).toInt << 8) | (inputBuffer(i).toInt & 0xFF)
                  i += 2
                }
                ({
                  c += 1; c - 1
                })
              }
              sum += (sample * sample).toDouble
            }
          }
        }
      }
    }
    else {
      var i: Int = 0
      while (i < bytesRead) {
        {
          var longSample: Long = 0
          var c: Int = 0
          while (c < channels) {
            {
              if (inputFormat.isBigEndian) {
                sample = inputBuffer(({
                  i += 1; i - 1
                })).toInt
                var b: Int = 1
                while (b < sampleSizeInBytes) {
                  sample = (sample << 8) | (inputBuffer(i).toInt & 0xFF)
                  ({
                    b += 1; b - 1
                  })
                  ({
                    i += 1; i - 1
                  })
                }
              }
              else {
                sample = 0
                var b: Int = 0
                b = 0
                while (b < sampleSizeInBytes - 1) {
                  sample |= (inputBuffer(i).toInt & 0xFF) << (b * 8)
                  ({
                    b += 1; b - 1
                  })
                  ({
                    i += 1; i - 1
                  })
                }
                sample |= (inputBuffer(({
                  i += 1; i - 1
                })).toInt) << (b * 8)
              }
              longSample += sample
            }
            ({
              c += 1; c - 1
            })
          }
          sum += longSample.toDouble * longSample.toDouble
        }
      }
    }
    System.out.println("processWindow returns " + Math.sqrt(sum) / normalise)
    return Math.sqrt(sum) / normalise
  }

  def skipTo(time: Double) {
    jumpPosition = time * frameRate.round * frameSize
    if (jumpPosition > fileLength) jumpPosition = fileLength
  }

  def skipTo(thousandths: Int) {
    jumpPosition = fileLength / frameSize * thousandths / 1000 * frameSize
  }

  protected def skipAudio {
    var toSkip: Long = jumpPosition
    var hasSkipped: Long = 0
    if (jumpPosition >= bytePosition) toSkip -= bytePosition
    else if (jumpPosition < bytePosition) resetAudioFile
    try {
      while (toSkip > hasSkipped) {
        {
          val skipped: Long = in.skip(toSkip - hasSkipped)
          if (skipped <= 0) throw new IOException("skip() error: " + skipped + " returned")
          hasSkipped += skipped
        }
      }
    }
    catch {
      case e: IOException => {
        e.printStackTrace
      }
    }
    bytePosition += hasSkipped
    if (out != null) out.flush
    val currentPoint: Int = (blockCount - 1) / AudioWorm.averageCount
    blockCount = (bytePosition / inputBuffer.length.toLong).toInt
    if (wormData != null) {
      val stop: Int = Math.min(wormData.outTempo.length, (blockCount - 1) / AudioWorm.averageCount)
      var start: Int = Math.max(stop - WormConstants.wormLength, 0)
      if (currentPoint < stop) start = Math.max(start, currentPoint)
    }
    jumpPosition = -1
  }
}