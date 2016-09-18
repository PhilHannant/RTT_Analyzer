package data

import java.io.FileWriter

import play.api.libs.json._

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser JSONParser, creates the html results page
  */

case class JSONParser() {

  var jsonFull: String = "["
  var jsonStats: String = "["

  /** WormAnalyser write json method */

  def write(obj: WormAnalyser): String = {

    implicit val tempoWrites = new Writes[Tempo] {
      def writes(tempo: Tempo) = Json.obj(
        "tempo" -> tempo.tempo,
        "expectedTempo" -> tempo.baseTempo,
        "difference" -> tempo.difference,
        "elapsedTime" -> tempo.timeElapsed
      )
    }

    implicit val statsWrites = new Writes[Stats] {
      def writes(stats: Stats) = Json.obj(
        "averageTempo" -> stats.averageTempo,
        "medianTempo" -> stats.medianDiff,
        "averageDiff" -> stats.averageDiff,
        "medianDiff" -> stats.medianDiff,
        "responseTime" -> stats.responseTime
      )
    }

    implicit val wormAnalyzerWrites = new Writes[WormAnalyser] {
      def writes(analyser: WormAnalyser) = Json.obj(
        "name" -> analyser.name,
        "buffer" -> analyser.buffer,
        "stats" -> analyser.stats

      )
    }

    val jsonNew = Json.toJson(obj)
    jsonFull += jsonNew.toString()
    jsonFull
  }

  /** DWTAnalyser write json method */

  def write(obj: DWTAnalyser): String = {

    implicit val tempoWrites = new Writes[Tempo] {
      def writes(tempo: Tempo) = Json.obj(
        "tempo" -> tempo.tempo,
        "expectedTempo" -> tempo.baseTempo,
        "difference" -> tempo.difference,
        "elapsedTime" -> tempo.timeElapsed
      )
    }

    implicit val statsWrites = new Writes[Stats] {
      def writes(stats: Stats) = Json.obj(
        "averageTempo" -> stats.averageTempo,
        "medianTempo" -> stats.medianDiff,
        "averageDiff" -> stats.averageDiff,
        "medianDiff" -> stats.medianDiff,
        "responseTime" -> stats.responseTime
      )
    }

    implicit val dwtAnalyzerWrites = new Writes[DWTAnalyser] {
      def writes(analyser: DWTAnalyser) = Json.obj(
        "name" -> analyser.name,
        "buffer" -> analyser.buffer,
        "stats" -> analyser.stats
      )
    }

    val jsonNew = Json.toJson(obj)
    jsonFull += "," + jsonNew.toString()
    jsonFull
  }

  /** BeatrootAnalyser write json method */

  def write(obj: BeatrootAnalyser): String = {

    implicit val tempoWrites = new Writes[Tempo] {
      def writes(tempo: Tempo) = Json.obj(
        "tempo" -> tempo.tempo,
        "expectedTempo" -> tempo.baseTempo,
        "difference" -> tempo.difference,
        "beatCount" -> tempo.beatCount,
        "elapsedTime" -> tempo.timeElapsed
      )
    }

    implicit val statsWrites = new Writes[Stats] {
      def writes(stats: Stats) = Json.obj(
        "averageTempo" -> stats.averageTempo,
        "medianTempo" -> stats.medianDiff,
        "averageDiff" -> stats.averageDiff,
        "medianDiff" -> stats.medianDiff,
        "totalBeatCount" -> stats.totalBeatCount,
        "responseTime" -> stats.responseTime
      )
    }

    implicit val beatrootAnalyzerWrites = new Writes[BeatrootAnalyser] {
      def writes(analyser: BeatrootAnalyser) = Json.obj(
        "name" -> analyser.name,
        "buffer" -> analyser.buffer,
        "stats" -> analyser.stats
      )
    }



    val jsonNew = Json.toJson(obj)
    jsonFull += "," + jsonNew.toString() + "]"
    jsonFull
  }
  /** flush all json data to disk */
  def flushFull(path: String) = {

    val file = new FileWriter(path)
    file.write(jsonFull)
    file.flush()
    file.close()

  }

  /** WormAnalyser statshelper write json method */

  def writeStatsHelper(obj: WormAnalyser): String = {

    implicit val statsWrites = new Writes[Stats] {
      def writes(stats: Stats) = Json.obj(
        "averageTempo" -> stats.averageTempo,
        "medianTempo" -> stats.medianDiff,
        "averageDiff" -> stats.averageDiff,
        "medianDiff" -> stats.medianDiff,
        "responseTime" -> stats.responseTime
      )
    }

    implicit val wormAnalyzerWrites = new Writes[WormAnalyser] {
      def writes(analyser: WormAnalyser) = Json.obj(
        "name" -> analyser.name,
        "stats" -> analyser.stats
      )
    }
    val jsonNew = Json.toJson(obj)

    jsonStats += jsonNew.toString()
    jsonStats
  }

  /** DWTAnalyser statshelper write json method */
  def writeStatsHelper(obj: DWTAnalyser): String = {
    implicit val statsWrites = new Writes[Stats] {
      def writes(stats: Stats) = Json.obj(
        "averageTempo" -> stats.averageTempo,
        "medianTempo" -> stats.medianDiff,
        "averageDiff" -> stats.averageDiff,
        "medianDiff" -> stats.medianDiff,
        "responseTime" -> stats.responseTime
      )
    }

    implicit val dwtAnalyzerWrites = new Writes[DWTAnalyser] {
      def writes(analyser: DWTAnalyser) = Json.obj(
        "name" -> analyser.name,
        "stats" -> analyser.stats
      )
    }

    val jsonNew = Json.toJson(obj)
    jsonStats += "," + jsonNew.toString()
    jsonStats
  }

  /** BeatrootAnalyser statshelper write json method */
  def writeStatsHelper(obj: BeatrootAnalyser): String = {
    implicit val statsWrites = new Writes[Stats] {
      def writes(stats: Stats) = Json.obj(
        "averageTempo" -> stats.averageTempo,
        "medianTempo" -> stats.medianDiff,
        "averageDiff" -> stats.averageDiff,
        "medianDiff" -> stats.medianDiff,
        "totalBeatCount" -> stats.totalBeatCount,
        "responseTime" -> stats.responseTime
      )
    }

    implicit val beatrootAnalyzerWrites = new Writes[BeatrootAnalyser] {
      def writes(analyser: BeatrootAnalyser) = Json.obj(
        "name" -> analyser.name,
        "stats" -> analyser.stats
      )
    }



    val jsonNew = Json.toJson(obj)
    jsonStats += "," + jsonNew.toString() + "]"
    jsonStats
  }


  /** write all json data */
  def writeAll(worm: WormAnalyser, dwt: DWTAnalyser, beat: BeatrootAnalyser) = {
    writeStatsHelper(worm)
    writeStatsHelper(dwt)
    writeStatsHelper(beat)
    write(worm)
    write(dwt)
    write(beat)
  }
  /** flush json stats to disk */
  def flushStats(path: String) = {

    val file = new FileWriter(path)
    file.write(jsonStats)
    file.flush()
    file.close()

  }

}
