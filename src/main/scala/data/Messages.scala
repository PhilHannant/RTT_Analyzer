package data

import akka.actor.ActorRef


/**
  * Created by philhannant on 01/08/2016.
  */
sealed trait Messages
case class StartLiveAudio(expectedBPM: Double, processingActor: ActorRef) extends Messages
case class EndLiveAudio(processingActor: ActorRef) extends Messages
case class ProcessBytes(data: Array[Byte]) extends Messages
case class NewTempoWorm(tempo: Double) extends Messages
case class NewTempoDwt(tempo: Double) extends Messages
case class NewTempoBeatroot(tempo: Double, beatCount: Double) extends Messages
case class ParseJSON() extends Messages
case class WriteStatsJSON() extends Messages
case class SendExpectedBPM(bpm: Double) extends Messages
case class SendBeatRoot(data: Array[Byte], processingActor: ActorRef) extends Messages
case class SendDwt(data: Array[Byte], processingActor: ActorRef) extends Messages
case class Close() extends Messages