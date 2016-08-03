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
  val liveAudioProcessorActor = context.actorOf(Props[LiveAudioProcessorActor], "dwtProcessor")
  val dataCollectionActor = context.actorOf(Props[DataCollectorActor], "dataCollector")
  val w: Worm = new Worm(dataCollectionActor)

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
       context.system.terminate()
       System.exit(0)
   }
}


