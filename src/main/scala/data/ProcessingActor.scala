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

  private var path: String = _
  def path (value: String):Unit = path = value

  private var timeAtStart: Long = _
  def timeAtStart (value: Long): Unit = timeAtStart = value

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
    case SendInputs(bpm, filePath, startTime) =>
      expectedBpm(bpm)
      path(filePath)
      timeAtStart(startTime)
    case ProcessBytes(data) =>
      println("got it")
      beatrootWorker ! SendBeatRoot(data, self)
      dwtWorker ! SendDwt(data, self)
    case NewTempoDwt(tempo, currentTime) =>
      val t = Tempo(tempo, expectedBpm, None, currentTime - timeAtStart)
      gui.updateDwt(tempo)
      dwtStatsBuffer += t
      dWtAnalyser.addTempo(t)
    case NewTempoWorm(tempo, currentTime) =>
      val t = Tempo(tempo, expectedBpm, None, currentTime - timeAtStart)
      if (count == 5) {
        gui.updateWorm(tempo)
        count = 0
      }
      count = count + 1
      wormStatsuffer += t
      wormAnalyser.addTempo(t)
    case NewTempoBeatroot(tempo, beatCount, currentTime) =>
      val t = Tempo(tempo, expectedBpm, Some(beatCount), currentTime - timeAtStart)
      gui.updatebrt(tempo, beatCount)
      beatStatsBpmBuffer += t
      beatrootAnalyser.addTempo(t)
    case WriteJSON =>
      addStats()
      jsonParser.writeAll(wormAnalyser, dWtAnalyser, beatrootAnalyser)
      jsonParser.flushStats(path)
      jsonParser.flushFull(path)
  }


  def addStats() = {

    def addStatsHelper(lb: ListBuffer[Tempo]): Stats = {
      val sc = StatsCalculator()
      Stats(sc.getAverage(lb, "tempo"),
        sc.getMedian(lb, "tempo"),
        sc.getAverage(lb, "diffs"),
        sc.getMedian(lb, "diffs"),
        sc.getTotal(lb),
        sc.getResponseTime(lb.toList))
    }

    dWtAnalyser.stats = Some(addStatsHelper(dwtStatsBuffer))
    wormAnalyser.stats = Some(addStatsHelper(wormStatsuffer))
    beatrootAnalyser.stats = Some(addStatsHelper(beatStatsBpmBuffer))

  }


}

