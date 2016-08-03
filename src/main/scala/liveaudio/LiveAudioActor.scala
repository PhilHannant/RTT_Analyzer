package liveaudio



import akka.actor._
import akka.routing._
import at.ofai.music.worm.{Worm, WormControlPanel}
import data._
import dwtbpm.WaveletBPMDetector

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by philhannant on 01/08/2016.
  */
class LiveAudioActor extends Actor with ActorLogging{

  val soundCaptureImpl = new SoundCaptureImpl
  val processingActor = context.actorOf(Props[ProcessingActor], "processor")
  val w: Worm = new Worm(processingActor)

   def receive = {
     case StartLiveAudio =>
       println(self.toString())
       w.play()
     case EndLiveAudio =>
       println("end")
       context.system.terminate()
       System.exit(0)
     case _ =>
       println("help")
       processingActor ! ParseJSON

   }
}


