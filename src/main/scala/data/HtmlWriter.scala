package data

import java.io.FileWriter
import java.lang.reflect.Field

import scala.annotation.tailrec


/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser HTMLWriter, creates the html results page
  */

object HtmlWriter {

  val html = new StringBuilder

  /**  Creates a html table using the htmlhelper method */
  @tailrec
  def writeHtml(list: List[Analyser]): String = list match {
    case (x :: xs) => htmlHelper(x); writeHtml(xs)
    case Nil => html.toString()
  }

  /**  Htmlhelper method */
  def htmlHelper(analyser: Analyser): StringBuilder = {
    val heading = analyser.getClass.getDeclaredFields
    val stats = unWrapStats(analyser.stats)
    val shead = getStats(analyser.stats).getClass.getDeclaredFields

    html ++= "<table><thead>"
    html ++= "<tr><th>" + heading(0).getName + "</th>"
    html ++= "<th>" + heading(2).getName + "</th></tr></thead>"
    html ++= "<tbody><tr><td>" + analyser.name + "</td>"

    addData(stats, shead, html, 0)
    html ++= "</tbody></table></br><table><thead><tr>"
    getTempoHead(analyser.buffer.head.getClass.getDeclaredFields.toList, html)
    html ++= "</tr></thead><tbody>"
    addTempoData(analyser.buffer.toList, html)
    html ++= "</tbody></table></br>"
    html
  }

  /**  Adds heading according to the constructor names */
  @tailrec
  def addData(list: List[Double], heading: Array[Field], htmlString: StringBuilder ,acc: Int): StringBuilder = list match {
    case Nil => htmlString
    case (x :: xs) => addData(xs, heading, htmlString ++= "<tr><td>" + heading(acc).getName + "</td><td>" + x + "</td></tr>", acc + 1 )
  }

  /**  unwraps and option values */
  def unWrapStats(stats: Option[Stats]): List[Double] = stats match {
    case None => Nil
    case Some(x) => List(x.averageTempo, x.medianTempo, x.averageDiff, x.medianDiff, x.totalBeatCount, x.responseTime)
  }

  /**  Returns any stats values */
  def getStats(stats: Option[Stats]): Stats = stats match {
    case Some(x) => x
    case None => throw new RuntimeException("No Stats Value")
  }

  /** Gets the tempo heading */
  @tailrec
  def getTempoHead(list: List[Field], htmlString: StringBuilder): StringBuilder = list match {
    case (x :: xs) =>  getTempoHead(xs, htmlString ++= "<th>" + x.getName + "</th>")
    case Nil => htmlString
  }

  /** returns the tempo data */
  @tailrec
  def addTempoData(list: List[Tempo], htmlString: StringBuilder): StringBuilder = list match {
    case (x :: xs) => addTempoData(xs, htmlString ++= "<tr><td>" + x.tempo + "</td><td>" + x.baseTempo +
      "</td><td>" + x.difference + "</td><td>" + x.beatCount + "</td><td>" + x.timeElapsed + "</td></tr>")
    case Nil => htmlString
  }

  /** flushes the html tables held in the stringbuilder to disk*/
  def flush(path: String) = {
    html.insert(0, "<head>" + path + "<head>")
    val file = new FileWriter(path)
    file.write(html.toString())
    file.flush()
    file.close()
    html.clear()
  }

}
