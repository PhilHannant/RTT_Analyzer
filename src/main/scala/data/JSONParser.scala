package data

import play.api.libs.json._
import liveaudio.Tempo
import liveaudio.DWTAnalyser
/**
  * Created by philhannant on 31/07/2016.
  */
case class JSONParser() {


  def write(obj: DWTAnalyser): String = {

    implicit val tempoWrites = new Writes[Tempo] {
      def writes(tempo: Tempo) = Json.obj(
        "tempo" -> tempo.tempo,
        "expectedTempo" -> tempo.baseTempo,
        "difference" -> tempo.difference
      )
    }

    implicit val analyzerWrites = new Writes[DWTAnalyser] {
      def writes(analyzer: DWTAnalyser) = Json.obj(
        "name" -> analyzer.name,
        "buffer" -> analyzer.buffer
      )
    }

    val json2 = Json.toJson(obj)
    println(json2)
    json2.toString()
  }
}
