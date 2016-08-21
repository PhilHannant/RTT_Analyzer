package data

import java.io.FileWriter

import play.api.libs.json._

/**
  * Created by philhannant on 31/07/2016.
  */
case class JSONParser() {

  var jsonFull: String = "["
  var jsonStats: String = "["

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
        "medianDiff" -> stats.medianDiff
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
    println("json at end of worm " + jsonFull)
    jsonFull
  }

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
        "medianDiff" -> stats.medianDiff
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
    println("json at end of dwt " + jsonFull)
    jsonFull
  }


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
        "totalBeatCount" -> stats.totalBeatCount
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
    println("json at end of beatroot " + jsonFull)
    jsonFull
  }

  def flushFull(path: String) = {

    val file = new FileWriter(path)
    file.write(jsonFull)
    file.flush()
    file.close()

  }


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
    println("json at end of dwt " + jsonStats)
    jsonStats
  }

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
    val test = Json.prettyPrint(jsonNew)
    println(test)
    jsonStats
  }


  def writeAll(worm: WormAnalyser, dwt: DWTAnalyser, beat: BeatrootAnalyser) = {
    writeStatsHelper(worm)
    writeStatsHelper(dwt)
    writeStatsHelper(beat)
    write(worm)
    write(dwt)
    write(beat)
  }

  def flushStats(path: String) = {

    val file = new FileWriter(path)
    file.write(jsonStats)
    file.flush()
    file.close()

  }

}
