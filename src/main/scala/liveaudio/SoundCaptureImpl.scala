package liveaudio

import java.io._
import javax.sound.sampled._

import at.ofai.music.worm.{AudioWorm, Worm}
import dwtbpm.WaveletBPMDetector
import realTimeSoundCapture.SoundCapture
import scala.collection.mutable.Queue

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * adapted by Phil Hannant for MSc Computer Science project
  *
  * Original sound capture class before being replaced by the performance worm's live audio function
  */


class SoundCaptureImpl() {

  val dataBuffer = new Queue[Array[Byte]]


  private var audioProcessor: LiveAudioProcessor = _
  def audioProcessor (value: LiveAudioProcessor):Unit = audioProcessor = value

  var windowsProcessed: Int = 0
  val wormChunk = 1764
  var wormRun = false
  val sampleSize = 524288
  val sampleRate: Float = 44100
  val bitsPerSample: Int = 16
  val channels: Int = 2
  val signed: Boolean = true
  val bigEndian: Boolean = true
  val inputFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 16, 2, 4, sampleRate, false)
  val format: AudioFormat = new AudioFormat(sampleRate, bitsPerSample, channels, signed, bigEndian)
  var input: TargetDataLine = null
  val inputStream: AudioInputStream = null
  val sourceDataLine: SourceDataLine = null
  val dataLine: DataLine = null
  var outputStream: ByteArrayOutputStream = null
  var bytesRead: Int = 0
  var streamedBytes: Int = 0
  var data: Array[Byte] = null
  val recordLength: Long = 15000
  var status: Boolean = false

  def startCapture: Int = {

    try {

      input = AudioSystem.getTargetDataLine(inputFormat)
      val info: DataLine.Info = new DataLine.Info(classOf[TargetDataLine], inputFormat)
      input = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
      input.open(inputFormat)
      outputStream = new ByteArrayOutputStream
      input.start()



      val currentTime: Long = System.currentTimeMillis()
      val finishTime: Long = currentTime + recordLength
      bytesRead = 0
      status = true
      while (System.currentTimeMillis() < finishTime) {
        data = new Array[Byte](wormChunk)
        outputStream = new ByteArrayOutputStream
        while (bytesRead < 17640) {
            streamedBytes = input.read(data, 0, wormChunk)
            bytesRead += streamedBytes
            outputStream.write(data, 0, streamedBytes)
            data = outputStream.toByteArray
            var f = Future {
              runWorm(data)
            }
          }
          bytesRead = 0
          outputStream.close()

        }

        return bytesRead
      }

    catch {
      case ex: LineUnavailableException => {
        ex.printStackTrace
      }
    }
    return 0
  }

  def recieve(bytes: Array[Byte]) = {
    var f = Future {
      run(bytes)
    }
  }

  def run(toProcess: Array[Byte]) = {
    windowsProcessed = windowsProcessed + 1
    println(windowsProcessed)
    val ap = new LiveAudioProcessor
    ap.addData(toProcess)
    val dwtbpm = new WaveletBPMDetector(
      ap,
      131072,
      WaveletBPMDetector.Daubechies4)
    val res = dwtbpm.bpm()
    println(res + " bpm")
    println("dwt")
    println("returned" + windowsProcessed)
    Thread.sleep(1000)
    System.exit(0)
    if (windowsProcessed == 3) System.exit(0)
  }

  def getWindowsProcessed() ={
    windowsProcessed
  }

  def getWormInstance(): Boolean = {
    wormRun
}

  def runWorm(bytes: Array[Byte]) = {
    wormRun = true
//    aw.nextBlock(bytes, wormChunk)
  }
}




object SoundCaptureImpl{

  def apply: SoundCaptureImpl = new SoundCaptureImpl()
}

