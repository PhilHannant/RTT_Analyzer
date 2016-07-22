package liveaudio

/**
  * This class is based on a Java project by A.Greensted and available at
  * http://www.labbookpages.co.uk/audio/javaWavFiles.html
  */
class LiveAudioProcessor() {

  var bufferPointer: Int = 0
  val bytesPerSample: Int = 2
  val numberOfChannels: Int = 2
  val data = Array.fill(20)((scala.util.Random.nextInt(256) - 128).toByte)
  var frameCounter = 0

  def readSample(): Long = {

    def sampleReader(value: Long, acc: Int): Long = {
      acc match {
        case x if x < bytesPerSample => {
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

  def readFrames(sampleBuffer: Array[Int], offset: Int, numberOfFrames: Int): Int = {
    var pointer = offset

    for(i <- 0 until numberOfFrames){
      if (frameCounter == numberOfFrames) return i

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
    return numberOfFrames
  }
}
