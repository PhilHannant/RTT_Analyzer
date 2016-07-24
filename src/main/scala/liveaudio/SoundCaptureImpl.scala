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
  private var format: AudioFormat = new AudioFormat(sampleRate, bitsPerSample, channels, signed, bigEndian)
  private var input: TargetDataLine = null
  private val inputStream: AudioInputStream = null
  private val sourceDataLine: SourceDataLine = null
  private val dataLine: DataLine = null
  private var outputStream: ByteArrayOutputStream = null
  private var bytesRead: Int = 0
  private var streamedBytes: Int = 0
  private var data: Array[Byte] = null
  private val recordLength: Long = 30000
  private var status: Boolean = false

  def startCapture: Int = {
    try {
      input = AudioSystem.getTargetDataLine(format)
      val info: DataLine.Info = new DataLine.Info(classOf[TargetDataLine], format)
      input = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
      input.open(format)
      outputStream = new ByteArrayOutputStream

      input.start
      val currentTime: Long = System.currentTimeMillis
      val finishTime: Long = currentTime + recordLength
      bytesRead = 0
      status = true
      while (System.currentTimeMillis() < finishTime) {
        data = new Array[Byte](sampleSize)
        while (bytesRead < 524288) {

            streamedBytes = input.read(data, 0, sampleSize)
            bytesRead += streamedBytes
            outputStream.write(data, 0, streamedBytes)
            println(data(34))
          }
          val f = Future {
            run(outputStream.toByteArray)
          }
          bytesRead = 0
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
      this,
      ap,
      131072,
      WaveletBPMDetector.Daubechies4).bpm()
  }

  def getWindowsProcessed() ={
    windowsProcessed
  }
}


object SoundCaptureImpl{

  def apply: SoundCaptureImpl = new SoundCaptureImpl()
}