
import dwtbpm.WaveletBPMDetector
import org.scalatest._
import liveaudio.LiveAudioProcessor
import liveaudio.SoundCaptureImpl
/**
  * Created by philhannant on 20/07/2016.
  */
class LiveAudioProcessorTest extends FlatSpec {

  "A LiveAudioProcessor readSample" should "return an integer" in {
    val lap = new LiveAudioProcessor
    val returnedVal = lap.readSample()
    assert(returnedVal > 0 || returnedVal < 0 || returnedVal == 0)

  }

  "A LiveAudioProcessor readFrames" should "return an integer" in {
    val lap = new LiveAudioProcessor
    val data: Array[Int] = new Array[Int](20)
    val numberOfFrames = 5
    val returnedVal = lap.readFrames(data, 0, numberOfFrames)
    assert(returnedVal == numberOfFrames)
  }


}

class SoundCaptureImplTest extends FlatSpec {

  "A SoundCaptureImpl" should "send a byte array" in {
    val sci = new SoundCaptureImpl()
    val audioProcessor = new LiveAudioProcessor
    val dwtbpm = WaveletBPMDetector(
      sci,
      audioProcessor,
      131072,
      WaveletBPMDetector.Daubechies4)
    sci.audioProcessor(audioProcessor)
    sci.startCapture
    Thread.sleep(500)
    val result = audioProcessor.popData()
    assert(result != null)
  }


}

class WaveletBPMDetectorTest extends FlatSpec {

  "A WaveletBPMDectector" should "return an integer (bpm)" in {
    val sci = new SoundCaptureImpl()
    val audioProcessor = new LiveAudioProcessor
    val dwtbpm = WaveletBPMDetector(
      sci,
      audioProcessor,
      131072,
      WaveletBPMDetector.Daubechies4)
    sci.audioProcessor(audioProcessor)
    sci.startCapture
    val bpm = dwtbpm.bpm()
    println("bpm returned = " + bpm)
    assert(bpm > 60)
  }

}
