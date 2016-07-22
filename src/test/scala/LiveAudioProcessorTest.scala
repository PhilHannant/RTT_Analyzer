
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
    val dwtbpm = new WaveletBPMDetector()
    sci.startCapture
    val sentBytes = dwtbpm.popData()
    assert(sentBytes != null)

  }


}


class WaveletBPMDetectorTest extends FlatSpec {



}
