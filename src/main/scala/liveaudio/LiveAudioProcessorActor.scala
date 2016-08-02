package liveaudio

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import data.{Messages, ProcessBytes}
import dwtbpm.WaveletBPMDetector

/**
  * Created by philhannant on 02/08/2016.
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
