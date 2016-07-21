
import org.scalatest._
import liveaudio.LiveAudioProcessor
/**
  * Created by philhannant on 20/07/2016.
  */
class LiveAudioProcessorTest extends FlatSpec {

  "A LiveAudioProcessor readSample" should "return an integer" in {
    val lap = new LiveAudioProcessor
    val returnedVal = lap.readSample()
    assert(returnedVal > 0)

  }

//  "A LiveAudioProcessor readFrames" should "return an integer" in {
//    val lap = new LiveAudioProcessor
//    val data = Array.fill(20)((scala.util.Random.nextInt(256) - 128).toByte)
//    val numberOfFrames = 10
//    val returnedVal = lap.readFrames(data, numberOfFrames)
//    assert(returnedVal == numberOfFrames)
//  }
}
