package liveaudio

import java.io._
import javax.sound.sampled._

import dwtbpm.WaveletBPMDetector
import realTimeSoundCapture.SoundCapture

class SoundCaptureImpl() {

  private var dwtbpm: WaveletBPMDetector = _
  def dwtbpm (value: WaveletBPMDetector):Unit = dwtbpm = value


  val sampleSize = 1024
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
  private val recordLength: Long = 5000
  private var status: Boolean = false

  def startCapture: Int = {
    try {
      input = AudioSystem.getTargetDataLine(format)
      val info: DataLine.Info = new DataLine.Info(classOf[TargetDataLine], format)
      input = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
      input.open(format)
      outputStream = new ByteArrayOutputStream
      data = new Array[Byte](input.getBufferSize)
      input.start
      val currentTime: Long = System.currentTimeMillis
      val finishTime: Long = currentTime + recordLength
      bytesRead = 0
      status = true
      while (status) {
        {
          streamedBytes = input.read(data, 0, sampleSize)
          bytesRead += streamedBytes
          outputStream.write(data, 0, streamedBytes)
          dwtbpm.addData(outputStream.toByteArray)
        }
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



}


object SoundCaptureImpl{

  def apply: SoundCaptureImpl = new SoundCaptureImpl()
}