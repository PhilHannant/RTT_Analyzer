
import org.scalatest._
import liveaudio.LiveAudioProcessor
/**
  * Created by philhannant on 20/07/2016.
  */
class LiveAudioProcessorTest extends FlatSpec {

  "An AudioProcessor readSample" should "return an integer" in {
    val lap = new LiveAudioProcessor
    val data = Array.fill(20)((scala.util.Random.nextInt(256) - 128).toByte)
    val returnedVal = lap.readSample(data)
    assert(returnedVal > 0)

  }

}
