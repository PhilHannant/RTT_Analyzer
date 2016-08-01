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
       val w = new Worm
       val wcp: WormControlPanel = new WormControlPanel(w)
       wcp.actionPerformed("Play")
       Thread.sleep(15000)
       wcp.actionPerformed("Quit")
       System.out.print("end")
   }
}


