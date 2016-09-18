package liveaudio

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * This class is based on a Java project by A.Greensted and available at
  * http://www.labbookpages.co.uk/audio/javaWavFiles.html
  *
  * adapted by Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser WaveletBPMDetector liveaudioprocessing class, works on byte array sent by ProcessingActor
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

  /** method which populates buffer byte array with capture audio data for processing */
  def addData(data: Array[Byte]) = {
    buffer = data
  }

  /** First readFrames method */
  def readFrames(sampleBuffer: Array[Int], numberOfFrames: Int): Int = {
    readFrames(sampleBuffer, 0, numberOfFrames)
  }
  /** Overloaded readFrames method */
  def readFrames(sampleBuffer: Array[Int], offset: Int, numberOfFrames: Int): Int = {
    var pointer = offset

    for(i <- 0 until numberOfFrames){
      getSample(0)
      frameCounter = frameCounter + 1
    }

    /** tail recursive getSample helper method*/
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
    numberOfFrames
  }

  /** readsample method */
  def readSample(): Long = {

    /** tail recursive sample reader helper method reader, performs byte shift where required */
    @tailrec
    def sampleReader(value: Long, acc: Int): Long = {
      acc match {
        case x if x < bytesPerSample => {
          var v: Int = buffer(bufferPointer)
          if (acc < bytesPerSample - 1 || bytesPerSample == 1) v = v & 0xFF
          bufferPointer = bufferPointer + 1
          sampleReader(value + (v << (acc * 8)), acc + 1)
        }
        case x if x == bytesPerSample => value
        case _ => value
      }
    }

    sampleReader(0L, 0)
  }



  def popData(): Array[Byte] = {
    if(dataBuffer.isEmpty) return null
    dataBuffer.dequeue()
  }
}
