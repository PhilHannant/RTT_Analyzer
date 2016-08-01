package liveaudio



import akka.actor._
import akka.routing._
import at.ofai.music.worm.{Worm, WormControlPanel}
import data.StartLiveAudio
/**
  * Created by philhannant on 01/08/2016.
  */
class LiveAudioActor extends Actor with ActorLogging{
   def receive = {
     case StartLiveAudio =>
       val w = new Worm(self)
       w.play()
       Thread.sleep(15000)
       System.exit(0)
       System.out.print("end")
   }
}


