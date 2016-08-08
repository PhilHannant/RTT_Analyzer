package data

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by philhannant on 02/08/2016.
  */
case class WormAnalyser(name: String, buffer: ListBuffer[Tempo], var stats: Option[Stats]) extends Analyser{

  def addTempo(tempo: Tempo) = {
    buffer += tempo
  }
}

object WormAnalyser{
  val sdf = new SimpleDateFormat("yyyy-mm-dd, HH:mm:ss")

  def apply (name: String): WormAnalyser = {
    val buffer = new ListBuffer[Tempo]()
    val resultDate = new Date(System.currentTimeMillis())
    val fullName = name + " WormAnalyser: " + sdf.format(resultDate)
    WormAnalyser(fullName, buffer, None)
  }
}