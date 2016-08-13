package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import at.ofai.music.beatroot.BeatRoot
import dwtbpm.WaveletBPMDetector
import liveaudio.LiveAudioProcessor

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by philhannant on 02/08/2016.
  */
class ProcessingActor extends Actor with ActorLogging{

  private var expectedBpm: Double = _
  def audioProcessor (value: Double):Unit = expectedBpm = value

  val wormAnalyser = WormAnalyser("worm", new ListBuffer[Tempo](), None)//placeholders
  val dWtAnalyser = DWTAnalyser("dwt", new ListBuffer[Tempo](), None)//placeholders
  val beatrootAnalyser = BeatrootAnalyser("beatroot", new ListBuffer[Tempo](), None)//placeholders
  val jsonParser = JSONParser()
  val dwtStatsBuffer = ListBuffer[Tempo]()
  val wormStatsuffer = ListBuffer[Tempo]()
  val beatStatsBpmBuffer = ListBuffer[Tempo]()
  val b: BeatRoot = new BeatRoot()
  b.audioProcessor.setInput()


  def receive = {
    case SendExpectedBPM(bpm: Double) =>

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
      b.gui.displayPanel.beatTrack(self)
    case NewTempoDwt(tempo, expected) =>
      val t = Tempo(tempo, expected, None)
      dwtStatsBuffer += t
      dWtAnalyser.addTempo(t)
    case NewTempoWorm(tempo, expected) =>
      val t = Tempo(tempo, expected, None)
      wormStatsuffer += t
      wormAnalyser.addTempo(t)
    case NewTempoBeatroot(tempo, expected, beatCount) =>
      val t = Tempo(tempo, expected, Some(beatCount))
      beatStatsBpmBuffer += t
      beatrootAnalyser.addTempo(t)
    case ParseJSON =>

      dWtAnalyser.stats = Some(addStats(dwtStatsBuffer))
      wormAnalyser.stats = Some(addStats(wormStatsuffer))
      beatrootAnalyser.stats = Some(addStats(beatStatsBpmBuffer))

      jsonParser.write(wormAnalyser)
      jsonParser.write(dWtAnalyser)
      jsonParser.write(beatrootAnalyser)
      jsonParser.flush("/Users/philhannant/Desktop/ActorTempoTest.json")
      context.system.terminate()
      System.exit(0)

  }

  def addStats(lb: ListBuffer[Tempo]): Stats = {
    val sc = StatsCalculator()
    Stats(sc.getAverage(lb, "tempo"),
      sc.getMedian(lb, "tempo"),
      sc.getAverage(lb, "diffs"),
      sc.getMedian(lb, "diffs"),
      sc.getTotal(lb))
  }

}
