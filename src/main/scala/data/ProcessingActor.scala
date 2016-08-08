package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import at.ofai.music.beatroot.BeatRoot
import dwtbpm.WaveletBPMDetector
import liveaudio.LiveAudioProcessor

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by philhannant on 02/08/2016.
  */
class ProcessingActor extends Actor with ActorLogging{


  val wormAnalyser = WormAnalyser("worm", new ListBuffer[Tempo](), None)//placeholders
  val dWtAnalyser = DWTAnalyser("dwt", new ListBuffer[Tempo](), None)//placeholders
  val beatrootAnalyser = BeatrootAnalyser("beatroot", new ListBuffer[Tempo](), None)//placeholders
  val jsonParser = JSONParser()
  val dwtStatsBuffer = ListBuffer[Tempo]()
  val wormStatsuffer = ListBuffer[Tempo]()
  val beatStatsBpmBuffer = ListBuffer[Tempo]()
  val sc = StatsCalculator()
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
      dwtStatsBuffer += t
      dWtAnalyser.addTempo(t)
    case NewTempoWorm(tempo, expected) =>
      val t = Tempo(tempo, expected)
      wormStatsuffer += t
      wormAnalyser.addTempo(t)
    case NewTempoBeatroot(tempo, expected) =>
      val t = Tempo(tempo, expected)
      beatStatsBpmBuffer += t
      beatrootAnalyser.addTempo(t)
    case ParseJSON =>

      dWtAnalyser.stats = Some(addStats(dwtStatsBuffer))


      jsonParser.write(wormAnalyser)
      jsonParser.write(dWtAnalyser)
      jsonParser.flush("/Users/philhannant/Desktop/ActorTempoTest.json")
      context.system.terminate()
      System.exit(0)

  }

  def addStats(lb: ListBuffer[Tempo]): Stats = {
    Stats(sc.getAverage(lb, "tempo"),
      sc.getMedian(lb, "tempo"),
      sc.getAverage(lb, "diffs"),
      sc.getMedian(lb, "diffs"))
  }

}
