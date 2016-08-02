package data

/**
  * Created by philhannant on 01/08/2016.
  */
sealed trait Messages
case class StartLiveAudio() extends Messages
case class EndLiveAudio() extends Messages
