package liveaudio

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import data.{Messages, ProcessBytes}
import dwtbpm.WaveletBPMDetector

/**
  * Original liveaudioProcessingActor no longer used
  */
class LiveAudioProcessorActor extends Actor with ActorLogging {

  def receive = {
    case ProcessBytes(data: Array[Byte]) =>
      println("got it")
      val ap = new LiveAudioProcessor
      ap.addData(data)
      val dwtbpm = new WaveletBPMDetector(
        ap,
        131072,
        WaveletBPMDetector.Daubechies4)
      val res = dwtbpm.bpm()
  }

}
