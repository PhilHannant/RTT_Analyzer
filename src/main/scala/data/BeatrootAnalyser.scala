package data

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by philhannant on 02/08/2016.
  */
case class BeatrootAnalyser(var name: String, buffer: ListBuffer[Tempo], var stats: Option[Stats]) extends Analyser{


  def addTempo(tempo: Tempo) = {
    buffer += tempo
  }

}

object BeatrootAnalyser{
  val sdf = new SimpleDateFormat("yyyy-mm-dd, HH:mm:ss")

  def apply (name: String): BeatrootAnalyser = {
    val buffer = ListBuffer[Tempo]()
    val resultDate = new Date(System.currentTimeMillis())
    val fullName = name + sdf.format(resultDate)
    BeatrootAnalyser(fullName, buffer, None)
  }
}