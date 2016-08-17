package data

import java.io.FileWriter

/**
  * Created by philhannant on 17/08/2016.
  */
class HtmlWriter {

  val html = new StringBuilder

  def writeHtml(worm: WormAnalyser, dwt: DWTAnalyser, beat: BeatrootAnalyser): String = {



    val heading = worm.getClass.getDeclaredFields
    val stats = unWrapStats(worm.stats)
    println(stats)
    val shead = getStats(worm.stats).getClass.getDeclaredFields

    html ++= "<table><thead>"
    html ++= "<tr><th>" + heading(0).getName + "</th>"
    html ++= "<th>" + heading(2).getName + "</th></tr></thead>"
    html ++= "<tbody><tr><td>" + worm.name + "</td>"
    html ++= "<tr><td>" + shead(0).getName + "</td><td>" + stats(0) + "</td></tr>"
    html ++= "<tr><td>" + shead(1).getName + "</td><td>" + stats(1) + "</td></tr>"
    html ++= "<tr><td>" + shead(2).getName + "</td><td>" + stats(2) + "</td></tr>"
    html ++= "<tr><td>" + shead(3).getName + "</td><td>" + stats(3) + "</td></tr></tbody></table>"

    html.toString()
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

}
