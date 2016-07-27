package liveaudio

import java.io._
import javax.sound.sampled._

import at.ofai.music.worm.{AudioWorm, Worm}
import dwtbpm.WaveletBPMDetector
import realTimeSoundCapture.SoundCapture

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SoundCaptureImpl() {

  private var audioProcessor: LiveAudioProcessor = _
  def audioProcessor (value: LiveAudioProcessor):Unit = audioProcessor = value

  val aw: AudioWorm = new AudioWorm
  var windowsProcessed: Int = 0
  val wormChunk = 1764
  var wormRun = false
  val sampleSize = 524288
  val sampleRate: Float = 44100
  val bitsPerSample: Int = 16
  val channels: Int = 2
  val signed: Boolean = true
  val bigEndian: Boolean = true
  val format: AudioFormat = new AudioFormat(sampleRate, bitsPerSample, channels, signed, bigEndian)
  var input: TargetDataLine = null
  val inputStream: AudioInputStream = null
  val sourceDataLine: SourceDataLine = null
  val dataLine: DataLine = null
  var outputStream: ByteArrayOutputStream = null
  var bytesRead: Int = 0
  var streamedBytes: Int = 0
  var data: Array[Byte] = null
  val recordLength: Long = 5000
  var status: Boolean = false

  def startCapture: Int = {
    try {
      input = AudioSystem.getTargetDataLine(format)
      val info: DataLine.Info = new DataLine.Info(classOf[TargetDataLine], format)
      input = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
      input.open(format)
      outputStream = new ByteArrayOutputStream

      input.start()
      val currentTime: Long = System.currentTimeMillis()
      val finishTime: Long = currentTime + recordLength
      bytesRead = 0
      status = true
      while (System.currentTimeMillis() < finishTime) {
        Thread.sleep(1000)
        data = new Array[Byte](wormChunk)
        outputStream = new ByteArrayOutputStream
        while (bytesRead < 1764) {


            streamedBytes = input.read(data, 0, wormChunk)
            bytesRead += streamedBytes

            outputStream.write(data, 0, streamedBytes)

            data = outputStream.toByteArray
            Thread.sleep(100)
            var f = Future {
              runWorm(data)
            }

          }

//          var f = Future {
//            run(data)
//          }
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

  def run(toProcess: Array[Byte]) = {
    windowsProcessed = windowsProcessed + 1
    println(windowsProcessed)
    val ap = new LiveAudioProcessor
    ap.addData(toProcess)
    val dwtbpm = new WaveletBPMDetector(
      ap,
      131072,
      WaveletBPMDetector.Daubechies4)
    dwtbpm.bpm()
  }

  def getWindowsProcessed() ={
    windowsProcessed
  }

  def getWormInstance(): Boolean = {
    wormRun
}

  def runWorm(bytes: Array[Byte]) = {
    wormRun = true
    for(a <- 0 until data.length)
      println(data(a))
    Thread.sleep(500)
    aw.nextBlock(data, wormChunk)
  }
}




object SoundCaptureImpl{

  def apply: SoundCaptureImpl = new SoundCaptureImpl()
}