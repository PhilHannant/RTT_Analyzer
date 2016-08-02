package data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}

/**
  * Created by philhannant on 02/08/2016.
  */
class DataCollectorActor extends Actor with ActorLogging{

  val wormAnalyser = WormAnalyser(null, null)//placeholders
  val dWtAnalyser = DWTAnalyser(null, null)//placeholders
  val jsonParser = JSONParser()

  def receive = {
    case NewTempoDwt(tempo, expected) =>
      val t = Tempo(tempo, expected)
      dWtAnalyser.addTempo(t)
    case NewTempoWorm(tempo, expected) =>
      val t = Tempo(tempo, expected)
      wormAnalyser.addTempo(t)
    case ParseJSON =>
      jsonParser.write(wormAnalyser)
      jsonParser.write(dWtAnalyser)
      jsonParser.flush("/Users/philhannant/Desktop/ActorTempoTest.json")

  }

}
