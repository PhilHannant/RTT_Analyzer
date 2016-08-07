package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import at.ofai.music.beatroot.BeatRoot
import dwtbpm.WaveletBPMDetector
import liveaudio.LiveAudioProcessor

import scala.collection.mutable.ArrayBuffer

/**
  * Created by philhannant on 02/08/2016.
  */
class ProcessingActor extends Actor with ActorLogging{


  val wormAnalyser = WormAnalyser("worm", new ArrayBuffer[Tempo]())//placeholders
  val dWtAnalyser = DWTAnalyser("dwt", new ArrayBuffer[Tempo]())//placeholders
  val beatrootAnalyser = BeatrootAnalyser("beatroot", new ArrayBuffer[Tempo]())//placeholders
  val jsonParser = JSONParser()
  val dwtBpmBuffer = ArrayBuffer[Double]()
  val wormBpmBuffer = ArrayBuffer[Double]()
  val beatrootBpmBuffer = ArrayBuffer[Double]()
  val b: BeatRoot = new BeatRoot()
  b.audioProcessor.setInput()


  def receive = {
    case ProcessBytes(data: Array[Byte]) =>
      println("got it")
      val ap = new LiveAudioProcessor
      ap.addData(data)
      val dwtbpm = new WaveletBPMDetector(
        ap,
        131072,
        WaveletBPMDetector.Daubechies4).bpm(self)
      b.audioProcessor.processFile(data)
      b.audioProcessor.setDisplay(b.gui.displayPanel) // after processing
      b.gui.updateDisplay(true)
      b.gui.displayPanel.beatTrack()
    case NewTempoDwt(tempo, expected) =>
      val t = Tempo(tempo, expected)
      dwtBpmBuffer += tempo
      dWtAnalyser.addTempo(t)
    case NewTempoWorm(tempo, expected) =>
      val t = Tempo(tempo, expected)
      wormAnalyser.addTempo(t)
    case NewTempoBeatroot(tempo, expected) =>
      val t = Tempo(tempo, expected)
      beatrootAnalyser.addTempo(t)
    case ParseJSON =>
      jsonParser.write(wormAnalyser)
      jsonParser.write(dWtAnalyser)
      jsonParser.flush("/Users/philhannant/Desktop/ActorTempoTest.json")
      context.system.terminate()
      System.exit(0)

  }



}
