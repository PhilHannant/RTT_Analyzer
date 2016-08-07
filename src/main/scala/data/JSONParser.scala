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


    implicit val wormAnalyzerWrites = new Writes[WormAnalyser] {
      def writes(analyzer: WormAnalyser) = Json.obj(
        "name" -> analyzer.name,
        "buffer" -> analyzer.buffer
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

    implicit val dwtAnalyzerWrites = new Writes[DWTAnalyser] {
      def writes(analyzer: DWTAnalyser) = Json.obj(
        "name" -> analyzer.name,
        "buffer" -> analyzer.buffer
      )
    }

    val jsonNew = Json.toJson(obj)
    json += "," + jsonNew.toString() + "]"
    println("json at end of dwt " + json)
    json
  }


  def write(obj: BeatrootAnalyser): String = {

    implicit val tempoWrites = new Writes[Tempo] {
      def writes(tempo: Tempo) = Json.obj(
        "tempo" -> tempo.tempo,
        "expectedTempo" -> tempo.baseTempo,
        "difference" -> tempo.difference
      )
    }

    implicit val beatrootAnalyzerWrites = new Writes[BeatrootAnalyser] {
      def writes(analyzer: BeatrootAnalyser) = Json.obj(
        "name" -> analyzer.name,
        "buffer" -> analyzer.buffer
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
