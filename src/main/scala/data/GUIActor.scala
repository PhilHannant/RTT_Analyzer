package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}

/**
  * Created by philhannant on 11/08/2016.
  */
class GUIActor extends Actor with ActorLogging{

  def receive: Receive = {
    case StartTestTimer(processingActor, liveAudioActor) =>
      println("in here")
      Thread.sleep(30000)
      liveAudioActor ! EndLiveAudio(processingActor)
  }
}
