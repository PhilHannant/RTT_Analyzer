package liveaudio

import java.io._
import javax.sound.sampled._

import dwtbpm.WaveletBPMDetector
import realTimeSoundCapture.SoundCapture

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SoundCaptureImpl() {

  private var audioProcessor: LiveAudioProcessor = _
  def audioProcessor (value: LiveAudioProcessor):Unit = audioProcessor = value

  var windowsProcessed: Int = 0
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
  val recordLength: Long = 30000
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
        data = new Array[Byte](sampleSize)
        outputStream = new ByteArrayOutputStream
        while (bytesRead < 524288) {


            streamedBytes = input.read(data, 0, sampleSize)
            bytesRead += streamedBytes
            outputStream.write(data, 0, streamedBytes)

            println(outputStream.size())
          }
          data = outputStream.toByteArray
          var f = Future {
            run(data)
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
    false
  }

}




object SoundCaptureImpl{

  def apply: SoundCaptureImpl = new SoundCaptureImpl()
}