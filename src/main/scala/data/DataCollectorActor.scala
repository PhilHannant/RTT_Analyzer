package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}

/**
  * Created by philhannant on 02/08/2016.
  */
class DataCollectorActor extends Actor with ActorLogging{

  val wormAnalyser = WormAnalyser(null, null)//placeholders
  val dWtAnalyser = DWTAnalyser(null, null)//placeholders

  def receive = {
    case NewTempoDwt(tempo, expected) =>
      val tempo = Tempo(tempo, expected)
      dWtAnalyser.addTempo(tempo)
    case NewTempoWorm(tempo, expected) =>
      val tempo = Tempo(tempo, expected)
      wormAnalyser.addTempo(tempo)
  }

}
