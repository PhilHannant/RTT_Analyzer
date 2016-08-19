package data

import java.io.FileWriter
import java.lang.reflect.Field

import scala.annotation.tailrec

/**
  * Created by philhannant on 17/08/2016.
  */
class HtmlWriter {

  val html = new StringBuilder

  def writeHtml(worm: WormAnalyser, dwt: DWTAnalyser, beat: BeatrootAnalyser): String = {


    def htmlHelper(analyser: Analyser): String = {
      val heading = analyser.getClass.getDeclaredFields
      val stats = unWrapStats(analyser.stats)
      println(stats)
      val shead = getStats(analyser.stats).getClass.getDeclaredFields

      html ++= "<table><thead>"
      html ++= "<tr><th>" + heading(0).getName + "</th>"
      html ++= "<th>" + heading(2).getName + "</th></tr></thead>"
      html ++= "<tbody><tr><td>" + analyser.name + "</td>"

      addData(stats, shead, html, 0)
      html ++= "</tbody></table>"
      html.toString()
    }

    htmlHelper(worm)

  }

  def flush(path: String) = {
    val file = new FileWriter(path)
    file.write(html.toString())
    file.flush()
    file.close()
  }

  def unWrapStats(stats: Option[Stats]): List[Double] = stats match {
    case None => Nil
    case Some(x) => List(x.averageTempo, x.medianTempo, x.averageDiff, x.medianDiff, x.totalBeatCount)
  }

  def getStats(stats: Option[Stats]): Stats = stats match {
    case Some(x) => x
  }


  def addData(list: List[Double], heading: Array[Field], htmlString: StringBuilder ,acc: Int): StringBuilder = list match {
    case Nil => htmlString
    case (x :: xs) => addData(xs, heading, htmlString ++= "<tr><td>" + heading(acc).getName + "</td><td>" + x + "</td></tr>", acc + 1 )
  }

}
