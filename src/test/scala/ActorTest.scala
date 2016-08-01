import akka.actor.{ActorSystem, Props}
import data.StartLiveAudio
import liveaudio.LiveAudioActor

/**
  * Created by philhannant on 01/08/2016.
  */
object ActorTest extends App{

  val system = ActorSystem("liveaudioActors")
  val coord = system.actorOf(Props(new LiveAudioActor()))
  //First message sent to coordinator to begin calculation
  coord ! StartLiveAudio


}
