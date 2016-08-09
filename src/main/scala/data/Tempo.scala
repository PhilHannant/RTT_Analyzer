package data

/**
  * Created by philhannant on 31/07/2016.
  */
case class Tempo(tempo: Double, baseTempo: Double, difference: Double, var beatCount: Option[Double]){

}
object Tempo{

  def apply(tempo: Double, baseTempo: Double, beatCount: Option[Double]): Tempo = {
    val difference = baseTempo - tempo
    Tempo(tempo, baseTempo, difference, beatCount)

  }

}




