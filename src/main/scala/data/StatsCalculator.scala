package data

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
  * Created by philhannant on 08/08/2016.
  */
case class StatsCalculator() {

  def getMedian(listBuff: ListBuffer[Tempo], identifer: String): Double =
    identifer match {
      case "tempo" =>  val list = getTempos(listBuff.toList); median(list)
      case "diffs" => val list = getDiffs(listBuff.toList); median(list)
  }

  def median(list: List[Double]): Double = {
    //adapted from the sample at Rosetta code
    val (lower, upper) = list.sortWith(_<_).splitAt(list.size / 2)
    if (list.size % 2 == 0) (lower.last + upper.head) / 2.0 else upper.head
  }

  def getAverage(listBuff: ListBuffer[Tempo], identifer: String): Double =
    identifer match {
      case "tempo" =>  val list = getTempos(listBuff.toList); average(list)
      case "diffs" => val list = getDiffs(listBuff.toList); average(list)
    }


  def average(list: List[Double]): Double = {

    @tailrec
    def averageTR(list: List[Double], size: Double, total: Double): Double = list match {
      case Nil => println(total); println(size); total/size
      case x :: xs => averageTR(xs, size + 1, total + x)

    }

    averageTR(list, 0, 0)
  }

  def getTotal(listBuff: ListBuffer[Tempo]): Double = {
    val list = getBeatCounts(listBuff.toList); totalbeatCount(list)
  }

  def totalbeatCount(list: List[Double]): Double = {

    @tailrec
    def totalHelper(lst: List[Double], total: Double): Double =
      lst match {
        case Nil => total
        case x :: xs => totalHelper(xs, total + x)
      }

    totalHelper(list, 0)
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

  def getBeatCounts(lst: List[Tempo]): List[Double] =
    lst match {
      case x :: xs => unwrapOption(x.beatCount) :: getBeatCounts(xs)
      case Nil => Nil
    }

  def unwrapOption(head: Option[Double]): Double =
    head match {
      case Some(value) => value
      case None => 0 //throw new RuntimeException("None present")
    }
}
