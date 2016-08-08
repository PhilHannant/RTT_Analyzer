package dwtbpm

import data.Tempo

import scala.collection.mutable.ListBuffer

/**
  * Created by philhannant on 08/08/2016.
  */
case class StatsCalculator() {

  def getMedian(listBuff: ListBuffer[Tempo], identifer: String): Double =
    identifer match {
      case "tempo" =>  val list = getTempos(listBuff.toList); median(list)
      case "diffs" => val list = getTempos(listBuff.toList); median(list)
  }

  def median(list: List[Double]): Double = {
    //adapted from the sample at Rosetta code
    val (lower, upper) = list.sortWith(_<_).splitAt(list.size / 2)
    if (list.size % 2 == 0) (lower.last + upper.head) / 2.0 else upper.head
  }

  def getTempos(lst: List[Tempo]): List[Double] =
     lst match {
       case x :: xs => x.tempo :: getTempos(xs)
       case Nil => Nil
  }

  def getDiffs(lst: List[Tempo]): List[Double] =
    lst match {
      case x :: xs => x.difference :: getDiffs(xs)
      case Nil => Nil
    }
}
