package data


import akka.actor.{Actor, ActorLogging}
import at.ofai.music.beatroot.BeatRoot
import dwtbpm.WaveletBPMDetector
import liveaudio.LiveAudioProcessor

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser BeatrootActor, instantiates Beatroot object and sends data for processing
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


/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser DwtActor, instantiates WaveletBPMDetector and LiveAudioProcessor
  * object and sends data for processing
  */

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

