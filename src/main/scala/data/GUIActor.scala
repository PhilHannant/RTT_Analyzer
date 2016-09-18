package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser GUIActor, starts the test process and ends it after 30 seconds
  */

class GUIActor extends Actor with ActorLogging{

  def receive: Receive = {
    case StartTestTimer(processingActor, liveAudioActor) =>
      Thread.sleep(30000)
      liveAudioActor ! EndLiveAudio(processingActor)
      GUI.htmlReady = true
  }
}
