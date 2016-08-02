package data

import java.sql.Date
import java.text.SimpleDateFormat

import scala.collection.mutable.ArrayBuffer

trait Analyser {
  def addTempo(tempo: Tempo)
}
/**
  * Created by philhannant on 31/07/2016.
  */
case class Tempo(tempo: Double, baseTempo: Double, difference: Double){

}
object Tempo{

  def apply(tempo: Double, baseTempo: Double): Tempo = {
    val difference = baseTempo - tempo
    Tempo(tempo, baseTempo, difference)

  }

}

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


case class WormAnalyser(name: String, buffer: ArrayBuffer[Tempo]) extends Analyser{


  def addTempo(tempo: Tempo) = {
    buffer += tempo
  }
}
object WormAnalyser{
  val sdf = new SimpleDateFormat("yyyy-mm-dd, HH:mm:ss")

  def apply (name: String): DWTAnalyser = {
    val buffer = new ArrayBuffer[Tempo]()
    val resultDate = new Date(System.currentTimeMillis())
    val fullName = name + " WormAnalyser: " + sdf.format(resultDate)
    DWTAnalyser(fullName, buffer)
  }
}