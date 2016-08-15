package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import at.ofai.music.beatroot.BeatRoot
import dwtbpm.WaveletBPMDetector
import liveaudio.LiveAudioProcessor

/**
  * Created by philhannant on 15/08/2016.
  */
class BeatrootActor extends Actor with ActorLogging{

  val b: BeatRoot = new BeatRoot()
  b.audioProcessor.setInput()



  def receive = {
    case SendBeatRoot(data, processingActor) =>
      b.audioProcessor.processFile(data)
      b.audioProcessor.setDisplay(b.gui.displayPanel) // after processing
      b.gui.updateDisplay(true)
      b.gui.displayPanel.beatTrack(processingActor)

  }

}


class DwtActor extends Actor with ActorLogging {


  def receive = {
    case SendDwt(data, processingActor) =>
      val ap = new LiveAudioProcessor
      ap.addData(data)
      val dwtbpm = new WaveletBPMDetector(
        ap,
        131072,
        WaveletBPMDetector.Daubechies4).bpm(processingActor)
  }

}

