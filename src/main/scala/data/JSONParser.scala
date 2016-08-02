package data

import java.io.FileWriter

import play.api.libs.json._

/**
  * Created by philhannant on 31/07/2016.
  */
case class JSONParser() {

  var json: String = ""

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
    println(jsonNew)
    json += jsonNew.toString()
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
    println(jsonNew)
    json += jsonNew.toString()
    json
  }

  def flush() = {

    val file = new FileWriter("/Users/philhannant/Desktop/Tempo.json")
    file.write(json)
    file.flush()
    file.close()

  }
}
