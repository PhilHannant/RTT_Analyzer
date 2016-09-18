package data

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * Implementation of the RTT_Analyser Analyser trait, holds the tempo values recorded by the Beatroot system
  *
  */
case class BeatrootAnalyser(var name: String, buffer: ListBuffer[Tempo], var stats: Option[Stats]) extends Analyser{


  def addTempo(tempo: Tempo) = {
    buffer += tempo
  }

}
/** companion object */
object BeatrootAnalyser{
  val sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss")

  def apply (name: String): BeatrootAnalyser = {
    val buffer = ListBuffer[Tempo]()
    val resultDate = new Date(System.currentTimeMillis())
    val fullName = name + sdf.format(resultDate)
    BeatrootAnalyser(fullName, buffer, None)
  }
}