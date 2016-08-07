package data

/**
  * Created by philhannant on 01/08/2016.
  */
sealed trait Messages
case class StartLiveAudio() extends Messages
case class EndLiveAudio() extends Messages
case class ProcessBytes(data: Array[Byte]) extends Messages
case class NewTempoWorm(tempo: Double, expected: Double) extends Messages
case class NewTempoDwt(tempo: Double, expected: Double) extends Messages
case class NewTempoBeatroot(tempo: Double, expected: Double) extends Messages
case class ParseJSON() extends Messages