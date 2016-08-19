package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import at.ofai.music.beatroot.BeatRoot
import dwtbpm.WaveletBPMDetector
import liveaudio.LiveAudioProcessor

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by philhannant on 02/08/2016.
  */
class ProcessingActor(beatrootWorker: ActorRef, dwtWorker: ActorRef) extends Actor with ActorLogging{

  private var expectedBpm: Double = _
  def expectedBpm (value: Double):Unit = expectedBpm = value

  val gui = GUI

  var count: Int = 0
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
      expectedBpm(bpm)
      println(bpm)
    case ProcessBytes(data: Array[Byte]) =>
      println("got it")
      beatrootWorker ! SendBeatRoot(data, self)
      dwtWorker ! SendDwt(data, self)
    case NewTempoDwt(tempo) =>
      val t = Tempo(tempo, expectedBpm, None)
      gui.updateDwt(tempo)
      dwtStatsBuffer += t
      dWtAnalyser.addTempo(t)
    case NewTempoWorm(tempo) =>
      val t = Tempo(tempo, expectedBpm, None)
      if (count == 5) {
        gui.updateWorm(tempo)
        count = 0
      }
      count = count + 1
      wormStatsuffer += t
      wormAnalyser.addTempo(t)
    case NewTempoBeatroot(tempo, beatCount) =>
      val t = Tempo(tempo, expectedBpm, Some(beatCount))

      gui.updatebrt(tempo, beatCount)
      beatStatsBpmBuffer += t
      beatrootAnalyser.addTempo(t)
    case ParseJSON =>

      dWtAnalyser.stats = Some(addStats(dwtStatsBuffer))
      wormAnalyser.stats = Some(addStats(wormStatsuffer))
      beatrootAnalyser.stats = Some(addStats(beatStatsBpmBuffer))

      jsonParser.write(wormAnalyser)
      jsonParser.write(dWtAnalyser)
      jsonParser.write(beatrootAnalyser)
      jsonParser.flush("/Users/philhannant/Desktop/ActorTempoTest_1.json")
      context.system.terminate()
      System.exit(0)
    case WriteStatsJSON =>
      dWtAnalyser.stats = Some(addStats(dwtStatsBuffer))
      wormAnalyser.stats = Some(addStats(wormStatsuffer))
      beatrootAnalyser.stats = Some(addStats(beatStatsBpmBuffer))

      jsonParser.writeStats(wormAnalyser)
      jsonParser.writeStats(dWtAnalyser)
      jsonParser.writeStats(beatrootAnalyser)
      jsonParser.flush("/Users/philhannant/Desktop/ActorTempoTest_2.json")

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

