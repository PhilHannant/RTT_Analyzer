package data

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by philhannant on 02/08/2016.
  */
case class DWTAnalyser(var name: String, buffer: ListBuffer[Tempo], var stats: Option[Stats]) extends Analyser{


  def addTempo(tempo: Tempo) = {
    buffer += tempo
  }
}

object DWTAnalyser{
  val sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss")

  def apply (name: String): DWTAnalyser = {
    val buffer = new ListBuffer[Tempo]()
    val resultDate = new Date(System.currentTimeMillis())
    val fullName = name + sdf.format(resultDate)
    DWTAnalyser(fullName, buffer, None)
  }
}