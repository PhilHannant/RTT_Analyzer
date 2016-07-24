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
  var buffer: Array[Byte] = _
  val dataBuffer: mutable.Queue[Array[Byte]] = new mutable.Queue[Array[Byte]]

  def readSample(): Long = {
    //      var value : Long = 0L
    //      for (b <- 0 until bytesPerSample) {
    ////        if (bufferPointer == bytesRead) {
    ////          val read : Int = iStream.read(buffer, 0, WavFile.BUFFER_SIZE)
    ////          if (read == -1) throw new WavFile.WavFileException("Not enough data available");
    ////          bytesRead = read;
    ////          println(read)
    ////          bufferPointer = 0;
    ////        }
    //
    //        var byteValue : Int = data(bufferPointer);
    //        if (b < bytesPerSample - 1 || bytesPerSample == 1)
    //          byteValue = byteValue & 0xFF;
    //        //println("b " + (b * 8))
    //        value = value + (byteValue << (b * 8));
    //        bufferPointer = bufferPointer + 1;
    //      }
    //      return value;

    def sampleReader(value: Long, acc: Int): Long = {


      acc match {
        case x if x < bytesPerSample => {
//          if(bytesRead == 131072) {
//            println("popping")
//            data = popData()
//            bytesRead = data.length
//            bufferPointer = 0
//          }
          var v: Int = buffer(bufferPointer);
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
//    println("popping")
//    data = popData()
//    println(data.length)
//    println(numberOfFrames)
    println(buffer(34))
    readFrames(sampleBuffer, 0, numberOfFrames)
  }

  def readFrames(sampleBuffer: Array[Int], offset: Int, numberOfFrames: Int): Int = {
    var pointer = offset

    for(i <- 0 until numberOfFrames){
      //if (dataBuffer.isEmpty) return i

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
    println(bufferPointer)
    numberOfFrames
  }

  def addData(data: Array[Byte]) = {
    buffer = data
    //dataBuffer += data
  }


  def popData(): Array[Byte] = {
    if(dataBuffer.isEmpty) return null
    dataBuffer.dequeue()
  }
}
