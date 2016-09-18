package data

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser StatsCalculator, carries out calculations required to populate a Stats object
  */

case class StatsCalculator() {

  /** Returns the median tempo of diff depending on input*/
  def getMedian(listBuff: ListBuffer[Tempo], identifer: String): Double =
    identifer match {
      case "tempo" =>  val list = getTempos(listBuff.toList); median(list)
      case "diffs" => val list = getDiffs(listBuff.toList); median(list)
  }

  /** Calculates the median value */
  def median(list: List[Double]): Double = {
    //adapted from the sample at Rosetta code
    val (lower, upper) = list.sortWith(_<_).splitAt(list.size / 2)
    if (list.size % 2 == 0) (lower.last + upper.head) / 2.0 else upper.head
  }

  /** Returns the average tempo of diff depending on input*/
  def getAverage(listBuff: ListBuffer[Tempo], identifer: String): Double =
    identifer match {
      case "tempo" =>  val list = getTempos(listBuff.toList); average(list)
      case "diffs" => val list = getDiffs(listBuff.toList); average(list)
    }

  /** Calculates the average value*/
  def average(list: List[Double]): Double = {

    @tailrec
    def averageTR(list: List[Double], size: Double, total: Double): Double = list match {
      case Nil => total/size
      case x :: xs => averageTR(xs, size + 1, total + x)

    }

    averageTR(list, 0, 0)
  }

  /** Returns the total beatcount*/
  def getTotal(listBuff: ListBuffer[Tempo]): Double = {
    val list = getBeatCounts(listBuff.toList); totalbeatCount(list)
  }

  /** Calculates the total beatcount*/
  def totalbeatCount(list: List[Double]): Double = {

    @tailrec
    def totalHelper(lst: List[Double], total: Double): Double =
      lst match {
        case Nil => total
        case x :: xs => totalHelper(xs, x)
      }

    totalHelper(list, 0)
  }

  /** Finds the time taken for a tempo result to be returned within 1 bpm of expected tempo*/
  def getResponseTime(list: List[Tempo]): Long = list match {
    case (x :: xs) => {
      if (x.difference < 1.0 && x.difference > -1.0) {
        x.timeElapsed
      }
      else getResponseTime(xs)
    }
    case Nil => 0
  }

  /** extracts tempo values from list*/
  def getTempos(lst: List[Tempo]): List[Double] =
     lst match {
       case x :: xs => x.tempo :: getTempos(xs)
       case Nil => Nil
  }
  /** extracts diference values from list*/
  def getDiffs(lst: List[Tempo]): List[Double] =
    lst match {
      case x :: xs => x.difference :: getDiffs(xs)
      case Nil => Nil
    }
  /** extracts beatcounts from list*/
  def getBeatCounts(lst: List[Tempo]): List[Double] =
    lst match {
      case x :: xs => unwrapOption(x.beatCount) :: getBeatCounts(xs)
      case Nil => Nil
    }
  /** Unwraps the option values held in the list*/
  def unwrapOption(head: Option[Double]): Double =
    head match {
      case Some(value) => value
      case None => 0
    }

}
