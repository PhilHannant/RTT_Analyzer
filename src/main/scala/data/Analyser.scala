package data

import scala.collection.mutable.ListBuffer

/**
  * Created by philhannant on 02/08/2016.
  */
trait Analyser {
  var name: String
  val buffer: ListBuffer[Tempo]
  var stats: Option[Stats]
  def addTempo(tempo: Tempo)

}
