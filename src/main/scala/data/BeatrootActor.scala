package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import at.ofai.music.beatroot.BeatRoot

/**
  * Created by philhannant on 15/08/2016.
  */
class BeatrootActor extends Actor with ActorLogging{

  val b: BeatRoot = new BeatRoot()
  b.audioProcessor.setInput()



  def receive = {
    case SendBeatRoot(data: Array[Byte], processingActor) =>
      b.audioProcessor.processFile(data)
      b.audioProcessor.setDisplay(b.gui.displayPanel) // after processing
      b.gui.updateDisplay(true)
      b.gui.displayPanel.beatTrack(processingActor)

  }

}


