package data

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.ListBuffer

/**
  * Created by philhannant on 02/08/2016.
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

object WormAnalyser {
  val sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss")

  def apply (name: String): WormAnalyser = {
    val buffer = new ListBuffer[Tempo]()
    val resultDate = new Date(System.currentTimeMillis())
    val fullName = name + sdf.format(resultDate)
    return new WormAnalyser(fullName, buffer, None)
  }

}