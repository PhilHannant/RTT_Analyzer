package data

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser tempo value encapsulation method
  */
case class Tempo(tempo: Double, baseTempo: Double, difference: Double, var beatCount: Option[Double], timeElapsed: Long){

}
object Tempo{

  def apply(tempo: Double, baseTempo: Double, beatCount: Option[Double], timeElapsed: Long): Tempo = {
    val difference = baseTempo - tempo
    Tempo(tempo, baseTempo, baseTempo - tempo, beatCount, timeElapsed)

  }

}




