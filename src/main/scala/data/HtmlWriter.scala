package data

import java.io.FileWriter

/**
  * Created by philhannant on 17/08/2016.
  */
class HtmlWriter {

  val html = new StringBuilder

  def writeHtml(worm: WormAnalyser, dwt: DWTAnalyser, beat: BeatrootAnalyser): String = {



    val heading = worm.getClass.getDeclaredFields

    html ++= "<table><thead>"
    html ++= "<tr><th>" + heading(0).getName + "</th>"
    html ++= "<th>" + heading(2).getName + "</th></tr></thead></table>"


    html.toString()
  }

  def flush(path: String) = {
    val file = new FileWriter(path)
    file.write(html.toString())
    file.flush()
    file.close()
  }

}
