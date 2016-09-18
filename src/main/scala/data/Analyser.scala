package data

import scala.collection.mutable.ListBuffer

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser Analyser trait, basis for holding calculated tempo records
  *
  */
trait Analyser {
  var name: String
  val buffer: ListBuffer[Tempo]
  var stats: Option[Stats]
  def addTempo(tempo: Tempo)

}
