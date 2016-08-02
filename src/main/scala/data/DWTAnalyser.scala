package data

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.ArrayBuffer

/**
  * Created by philhannant on 02/08/2016.
  */
case class DWTAnalyser(name: String, buffer: ArrayBuffer[Tempo]) extends Analyser{


  def addTempo(tempo: Tempo) = {
    buffer += tempo
  }
}

object DWTAnalyser{
  val sdf = new SimpleDateFormat("yyyy-mm-dd, HH:mm:ss")

  def apply (name: String): DWTAnalyser = {
    val buffer = new ArrayBuffer[Tempo]()
    val resultDate = new Date(System.currentTimeMillis())
    val fullName = name + " WormAnalyser: " + sdf.format(resultDate)
    DWTAnalyser(fullName, buffer)
  }
}