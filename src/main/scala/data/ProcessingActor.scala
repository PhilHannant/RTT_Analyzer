package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import dwtbpm.WaveletBPMDetector
import liveaudio.LiveAudioProcessor

import scala.collection.mutable.ArrayBuffer

/**
  * Created by philhannant on 02/08/2016.
  */
class ProcessingActor extends Actor with ActorLogging{


  val wormAnalyser = WormAnalyser("worm", new ArrayBuffer[Tempo]())//placeholders
  val dWtAnalyser = DWTAnalyser("dwt", new ArrayBuffer[Tempo]())//placeholders
  val jsonParser = JSONParser()
  val dwtBpmBuffer = ArrayBuffer[Double]()

  def receive = {
    case ProcessBytes(data: Array[Byte]) =>
      println("got it")
      val ap = new LiveAudioProcessor
      ap.addData(data)
      val dwtbpm = new WaveletBPMDetector(
        ap,
        131072,
        WaveletBPMDetector.Daubechies4).bpm(self)
    case NewTempoDwt(tempo, expected) =>
      val t = Tempo(tempo, expected)
      dwtBpmBuffer += tempo
      dWtAnalyser.addTempo(t)
    case NewTempoWorm(tempo, expected) =>
      val t = Tempo(tempo, expected)
      wormAnalyser.addTempo(t)
    case ParseJSON =>
      jsonParser.write(wormAnalyser)
      jsonParser.write(dWtAnalyser)
      jsonParser.flush("/Users/philhannant/Desktop/ActorTempoTest.json")
      context.system.terminate()
      System.exit(0)

  }

}
