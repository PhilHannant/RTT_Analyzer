package data

import akka.actor.ActorRef


/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * Sealed trait holding all of the messages used by the RTT_Analyser actor system
  */


sealed trait Messages
case class StartLiveAudio(expectedBPM: Double, processingActor: ActorRef, filePath: String, startTime: Long) extends Messages
case class EndLiveAudio(processingActor: ActorRef) extends Messages
case class ProcessBytes(data: Array[Byte]) extends Messages
case class NewTempoWorm(tempo: Double, currentTime: Long) extends Messages
case class NewTempoDwt(tempo: Double, currentTime: Long) extends Messages
case class NewTempoBeatroot(tempo: Double, beatCount: Double, currentTime: Long) extends Messages
case class WriteData() extends Messages
case class SendInputs(bpm: Double, filePath: String, startTime: Long) extends Messages
case class SendBeatRoot(data: Array[Byte], processingActor: ActorRef) extends Messages
case class SendDwt(data: Array[Byte], processingActor: ActorRef) extends Messages
case class Close() extends Messages
case class StartTestTimer(processingActor: ActorRef, liveAudioActor: ActorRef) extends Messages
case class Reset() extends Messages