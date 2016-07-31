package liveaudio

import scala.collection.mutable.ArrayBuffer

/**
  * Created by philhannant on 31/07/2016.
  */
case class Tempo(tempo: Double)


case class Analyzer(name: String){

  val buffer = new ArrayBuffer[Tempo]

  def addTempo(tempo: Tempo) = {
      buffer += tempo
  }
}