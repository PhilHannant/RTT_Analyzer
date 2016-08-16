package data

import java.io.FileWriter

import play.api.libs.json._

/**
  * Created by philhannant on 31/07/2016.
  */
case class JSONParser() {

  var json: String = "["

  def write(obj: WormAnalyser): String = {

    implicit val tempoWrites = new Writes[Tempo] {
      def writes(tempo: Tempo) = Json.obj(
        "tempo" -> tempo.tempo,
        "expectedTempo" -> tempo.baseTempo,
        "difference" -> tempo.difference
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
    json += jsonNew.toString()
    println("json at end of worm " + json)
    json
  }

  def write(obj: DWTAnalyser): String = {

    implicit val tempoWrites = new Writes[Tempo] {
      def writes(tempo: Tempo) = Json.obj(
        "tempo" -> tempo.tempo,
        "expectedTempo" -> tempo.baseTempo,
        "difference" -> tempo.difference
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
    json += "," + jsonNew.toString()
    println("json at end of dwt " + json)
    json
  }


  def write(obj: BeatrootAnalyser): String = {

    implicit val tempoWrites = new Writes[Tempo] {
      def writes(tempo: Tempo) = Json.obj(
        "tempo" -> tempo.tempo,
        "expectedTempo" -> tempo.baseTempo,
        "difference" -> tempo.difference,
        "beatCount" -> tempo.beatCount
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
    json += "," + jsonNew.toString() + "]"
    println("json at end of beatroot " + json)
    json
  }

  def flush(path: String) = {

    val file = new FileWriter(path)
    file.write(json)
    file.flush()
    file.close()

  }
}
