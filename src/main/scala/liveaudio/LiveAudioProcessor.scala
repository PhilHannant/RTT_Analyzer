package liveaudio

import scala.collection.mutable

/**
  * This class is based on a Java project by A.Greensted and available at
  * http://www.labbookpages.co.uk/audio/javaWavFiles.html
  */
class LiveAudioProcessor() {

  var bufferPointer: Int = 0
  val bytesPerSample: Int = 2
  var bytesRead: Int = 0
  val numberOfChannels: Int = 2
  var frameCounter = 0
  var data: Array[Byte] = _
  val dataBuffer: mutable.Queue[Array[Byte]] = new mutable.Queue[Array[Byte]]

  def readSample(): Long = {
    def sampleReader(value: Long, acc: Int): Long = {
      acc match {
        case x if x < bytesPerSample => {
          if(bytesRead == bufferPointer) {
            data = popData()
            bytesRead = data.length
            bufferPointer = 0
          }
          var v: Int = data(bufferPointer);
          if (acc < bytesPerSample - 1 || bytesPerSample == 1) v = v & 0xFF
            val valueNew = value + (v << (acc * 8))
            bufferPointer = bufferPointer + 1
            val newAcc = acc + 1
            sampleReader(valueNew, newAcc)
        }
        case x if x == bytesPerSample => value
        case _ => value
      }
    }

    sampleReader(0L, 0)
  }

  def readFrames(sampleBuffer: Array[Int], numberOfFrames: Int): Int = {
    println("readframes1")
    return readFrames(sampleBuffer, 0, numberOfFrames)
  }

  def readFrames(sampleBuffer: Array[Int], offset: Int, numberOfFrames: Int): Int = {
    println("framecountter at start" + frameCounter)
    var pointer = offset

    for(i <- 0 until numberOfFrames){
      if (dataBuffer.isEmpty) return i

      getSample(0)
      frameCounter = frameCounter + 1
    }

    def getSample(acc: Int): Int = {
      acc match{
        case x if x < numberOfChannels => {
          sampleBuffer(pointer) = readSample().toInt
          val newAcc = acc + 1
          pointer = pointer + 1
          getSample(newAcc)
        }
        case _ => acc
      }
    }
    println("pointer end " + pointer)
    println("framecountter at end" + frameCounter)
    return numberOfFrames
  }

  def addData(data: Array[Byte]) = {
    dataBuffer += data
  }

  def popData(): Array[Byte] = {
    println(dataBuffer.size)
    println(dataBuffer.nonEmpty)
    dataBuffer.dequeue()
  }
}
