package data


import akka.actor.{Actor, ActorLogging, ActorRef}
import at.ofai.music.beatroot.BeatRoot



import scala.collection.mutable.ListBuffer

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser ProcessingActor, carries out the bulk of the processing and sends captures bytes to beatrootActor and
  * DwtActor
  */
class ProcessingActor(beatrootWorker: ActorRef, dwtWorker: ActorRef) extends Actor with ActorLogging{

  private var expectedBpm: Double = _
  def expectedBpm (value: Double):Unit = expectedBpm = value

  private var path: String = _
  def path (value: String):Unit = {
    path = value
    wormAnalyser.name = wormAnalyser.name + " - " + path
    dWtAnalyser.name = dWtAnalyser.name +  " - " + path
    beatrootAnalyser.name = beatrootAnalyser.name + " - " + path
  }

  private var timeAtStart: Long = _
  def timeAtStart (value: Long): Unit = timeAtStart = value

  val gui = GUI
  val htmlWriter = HtmlWriter

  var count: Int = 0
  val wormAnalyser = WormAnalyser("worm: ")
  val dWtAnalyser = DWTAnalyser("dwt: ")
  val beatrootAnalyser = BeatrootAnalyser("beatroot: ")
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
        gui.updateWorm(tempo)//worm element of gui updated 5 time a second
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
    case WriteData =>
      addStats()
      jsonParser.writeAll(wormAnalyser, dWtAnalyser, beatrootAnalyser)
      jsonParser.flushFull(path + "full.json")
      htmlWriter.writeHtml(List(wormAnalyser, dWtAnalyser, beatrootAnalyser))
      htmlWriter.flush(path + "stats.html")
  }

  /** Populates the stats obejcts*/
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

