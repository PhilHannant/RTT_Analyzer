package data

/**
  * Created by philhannant on 02/08/2016.
  */
trait Analyser {

  val tempoStats: Stats

  def addTempo(tempo: Tempo)

  def addStats(stats: Stats)

}
