package liveaudio

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

case class WormAnalyser(name: String, buffer: ArrayBuffer[Tempo]) extends Analyser{


  def addTempo(tempo: Tempo) = {
    buffer += tempo
  }
}