import akka.actor.{ActorSystem, Props}
import data.{EndLiveAudio, ParseJSON, StartLiveAudio}
import liveaudio.LiveAudioActor

/**
  * Created by philhannant on 01/08/2016.
  */
object ActorTest extends App{

  val system = ActorSystem("liveaudioActors")
  val coord = system.actorOf(Props(new LiveAudioActor()))
  //First message sent to coordinator to begin calculation

  coord ! StartLiveAudio
  Thread.sleep(5000)
  coord ! ParseJSON
  Thread.sleep(2500)
  coord ! EndLiveAudio


}
