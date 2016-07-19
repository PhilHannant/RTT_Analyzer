package liveaudio

import java.io._
import javax.sound.sampled._

object SoundCaptureImpl {
  private var audioBuffer: Array[AnyRef] = null
}

class SoundCaptureImpl extends SoundCapture {
  sampleSize = 1024
  val sampleRate: Float = 44100
  val bitsPerSample: Int = 16
  val channels: Int = 2
  val signed: Boolean = true
  val bigEndian: Boolean = true
  format = new AudioFormat(sampleRate, bitsPerSample, channels, signed, bigEndian)
  SoundCaptureImpl.audioBuffer = new Array[AnyRef](10)
  private var format: AudioFormat = null
  private var input: TargetDataLine = null
  private val inputStream: AudioInputStream = null
  private val sourceDataLine: SourceDataLine = null
  private val dataLine: DataLine = null
  private var outputStream: ByteArrayOutputStream = null
  private var bytesRead: Int = 0
  private var streamedBytes: Int = 0
  private[realTimeSoundCapture] var sampleSize: Int = 0
  private var data: Array[Byte] = null
  private var audioObject: Any = null
  private val recordLength: Long = 5000
  private var status: Boolean = false
  private var read: Int = 0
  private var write: Int = 0

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
          audioObject = outputStream.toByteArray
          writeNext(audioObject)
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

  def getNext: Any = {
    val audioData: Any = SoundCaptureImpl.audioBuffer(read)
    if (read < SoundCaptureImpl.audioBuffer.length - 1) ({
      read += 1; read - 1
    })
    else read = 0
    return audioData
  }

  def writeNext(data: Any) {
    SoundCaptureImpl.audioBuffer(write) = data
    if (write < SoundCaptureImpl.audioBuffer.length - 1) ({
      write += 1; write - 1
    })
    else {
      write = 0
      status = false
    }
  }

  @throws[IOException]
  def close {
    input.close
    outputStream.close
  }

  def getReadPos: Int = {
    return read
  }
}