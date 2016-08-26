package data

import java.io.FileWriter
import java.lang.reflect.Field


/**
  * Created by philhannant on 17/08/2016.
  */
object HtmlWriter {

  val html = new StringBuilder

  def writeHtml(list: List[Analyser]): String = list match {
    case (x :: xs) => htmlHelper(x); writeHtml(xs)
    case Nil => html.toString()
  }

  def htmlHelper(analyser: Analyser): StringBuilder = {
    val heading = analyser.getClass.getDeclaredFields
    val stats = unWrapStats(analyser.stats)
    println(stats)
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


  def addData(list: List[Double], heading: Array[Field], htmlString: StringBuilder ,acc: Int): StringBuilder = list match {
    case Nil => htmlString
    case (x :: xs) => addData(xs, heading, htmlString ++= "<tr><td>" + heading(acc).getName + "</td><td>" + x + "</td></tr>", acc + 1 )
  }


  def unWrapStats(stats: Option[Stats]): List[Double] = stats match {
    case None => Nil
    case Some(x) => List(x.averageTempo, x.medianTempo, x.averageDiff, x.medianDiff, x.totalBeatCount, x.responseTime)
  }

  def getStats(stats: Option[Stats]): Stats = stats match {
    case Some(x) => x
  }

  def getTempoHead(list: List[Field], htmlString: StringBuilder): StringBuilder = list match {
    case (x :: xs) =>  getTempoHead(xs, htmlString ++= "<th>" + x.getName + "</th>")
    case Nil => htmlString
  }

  def addTempoData(list: List[Tempo], htmlString: StringBuilder): StringBuilder = list match {
    case (x :: xs) => addTempoData(xs, htmlString ++= "<tr><td>" + x.tempo + "</td><td>" + x.baseTempo +
      "</td><td>" + x.difference + "</td><td>" + x.beatCount + "</td><td>" + x.timeElapsed + "</td></tr>")
    case Nil => htmlString
  }


  def flush(path: String) = {
    html.insert(0, "<head>" + path + "<head>")
    val file = new FileWriter(path)
    file.write(html.toString())
    file.flush()
    file.close()
  }

}
