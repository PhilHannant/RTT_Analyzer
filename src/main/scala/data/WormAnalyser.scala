package data

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.ListBuffer

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * Implementation of the RTT_Analyser Analyser trait, holds the tempo values recorded by the Performance Worm
  *
  */
case class WormAnalyser(var name: String, buffer: ListBuffer[Tempo], var stats: Option[Stats]) extends Analyser{

  def addTempo(tempo: Tempo) = {
    if (buffer.isEmpty) buffer += tempo
    else if (checkTempo(tempo)) buffer += tempo
  }

  def checkTempo(t: Tempo) = {
    if (buffer.last.tempo != t.tempo) true
    else false
  }



}

/** companion object */
object WormAnalyser {
  val sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss")

  def apply (name: String): WormAnalyser = {
    val buffer = new ListBuffer[Tempo]()
    val resultDate = new Date(System.currentTimeMillis())
    val fullName = name + sdf.format(resultDate)
    return new WormAnalyser(fullName, buffer, None)
  }

}