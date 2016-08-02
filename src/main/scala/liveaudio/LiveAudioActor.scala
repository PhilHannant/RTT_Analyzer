package liveaudio



import akka.actor._
import akka.routing._
import at.ofai.music.worm.{Worm, WormControlPanel}
import data.{EndLiveAudio, ProcessBytes, StartLiveAudio}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by philhannant on 01/08/2016.
  */
class LiveAudioActor extends Actor with ActorLogging{

  val soundCaptureImpl = new SoundCaptureImpl


   def receive = {
     case StartLiveAudio =>
       val w = new Worm(self)
       println(self.toString())
       w.play()
       Thread.sleep(15000)
       System.exit(0)
       System.out.print("end")
     case EndLiveAudio =>
       println("end")
       context.system.terminate()
       System.exit(0)
     case ProcessBytes(data: Array[Byte]) =>
       var f = Future {
         soundCaptureImpl.run(data)
       }
     case "help" =>
       println("help")
       context.system.terminate()
       System.exit(0)
   }
}


