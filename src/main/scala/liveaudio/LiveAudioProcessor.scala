package liveaudio

/**
  * This class is based on a Java project by A.Greensted and available at
  * http://www.labbookpages.co.uk/audio/javaWavFiles.html
  */
class LiveAudioProcessor() {

  var bufferPointer: Int = 0
  val bytesPerSample: Int = 2

  def readSample(data: Array[Byte]): Long = {

    def sampleReader(data: Array[Byte], value: Long, acc: Int): Long = {
      acc match {
        case x if x < bytesPerSample => {
          var v: Int = data(bufferPointer);
          if (acc < bytesPerSample - 1 || bytesPerSample == 1) v = v & 0xFF
            val valueNew = value + (v << (acc * 8))
            bufferPointer = bufferPointer + 1
            val newAcc = acc + 1
            sampleReader(data, valueNew, newAcc)
        }
        case x if x == bytesPerSample => value
        case _ => value
      }
    }

    sampleReader(data, 0L, 0)
  }


}
