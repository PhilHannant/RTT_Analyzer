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

  val w: Worm = new Worm()


   def receive = {
     case StartLiveAudio(expectedBPM, processingActor, filePath, startTime) =>
       w.setActor(processingActor)
       println(self.toString())
       w.play()
       processingActor ! SendInputs(expectedBPM, filePath, startTime)
     case EndLiveAudio(processingActor) =>
       w.stop()
       Thread.sleep(2000)//allow for any processing to finish
       processingActor ! WriteData
//       processingActor ! Reset
     case Close =>
       context.system.terminate()
       System.exit(0)

     case _ =>
       println("help")


   }
}




