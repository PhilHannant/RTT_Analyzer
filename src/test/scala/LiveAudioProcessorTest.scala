
import dwtbpm.{ArrayOperations, WaveletBPMDetector}
import org.scalatest._
import liveaudio._
import at.ofai.music.worm._
import data._
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.io.Source
/**
  * Created by philhannant on 20/07/2016.
  */
class LiveAudioProcessorTest extends FlatSpec {

  "A LiveAudioProcessor readSample" should "return an integer" in {
    val lap = new LiveAudioProcessor
    val returnedVal = lap.readSample()
    assert(returnedVal > 0 || returnedVal < 0 || returnedVal == 0)

  }

  "A LiveAudioProcessor readFrames" should "return an integer" in {
    val lap = new LiveAudioProcessor
    val data: Array[Int] = new Array[Int](20)
    val numberOfFrames = 5
    val returnedVal = lap.readFrames(data, 0, numberOfFrames)
    assert(returnedVal == numberOfFrames)
  }


}

class SoundCaptureImplTest extends FlatSpec {

  "A SoundCaptureImpl" should "send a byte array" in {
    val sci = new SoundCaptureImpl()
    val audioProcessor = new LiveAudioProcessor
    val dwtbpm = WaveletBPMDetector(
      audioProcessor,
      131072,
      WaveletBPMDetector.Daubechies4)
    sci.audioProcessor(audioProcessor)
    sci.startCapture
    Thread.sleep(500)
    val result = audioProcessor.popData()
    assert(result != null)
  }

  "A SoundCaptureImpl" should "send an line to a Worm" in {
    val sci = new SoundCaptureImpl()
    val audioProcessor = new LiveAudioProcessor
    sci.audioProcessor(audioProcessor)
    sci.startCapture
    Thread.sleep(180000)
    val aw = sci.getWormInstance
    assert(aw == true)
  }

}

class WaveletBPMDetectorTest extends FlatSpec {

//  New test required as Future to be used to process bpm
//  "A WaveletBPMDectector" should "return an integer (bpm)" in {
//    val sci = new SoundCaptureImpl()
//    val audioProcessor = new LiveAudioProcessor
//    val dwtbpm = WaveletBPMDetector(
//      sci,
//      audioProcessor,
//      131072,
//      WaveletBPMDetector.Daubechies4)
//    sci.audioProcessor(audioProcessor)
//    sci.startCapture
//    val bpm = dwtbpm.bpm()
//    println("bpm returned = " + bpm)
//    assert(bpm > 60)
//  }

  "A WaveletBPMDectector" should "return an integer (bpm)" in {
    val sci = new SoundCaptureImpl()
    val audioProcessor = new LiveAudioProcessor
    sci.audioProcessor(audioProcessor)
    sci.startCapture
    Thread.sleep(31000)
    val windowsProcessed = sci.getWindowsProcessed()
    assert(windowsProcessed > 10)
  }

}

class TempoObjectTest extends FlatSpec {

  "A Tempo object " should "hold a tempo value" in {
    val t = Tempo(120, 120, None, System.currentTimeMillis())
    val result = t.tempo
    val expected = 120
    assertResult(expected)(result)
  }

  "A Tempo object" should "calculate the difference between 2 tempos" in {
    val t = Tempo(119.983827, 120, None, System.currentTimeMillis())
    val result = t.difference
    val expected = 120 - 119.983827
    assertResult(expected)(result)
  }

  "An Analyzer" should "take multiple tempo objects" in {
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125, 120, None, System.currentTimeMillis())
    val t3 = Tempo(135, 120, None, System.currentTimeMillis())
    val t4 = Tempo(145, 120, None, System.currentTimeMillis())
    val a = DWTAnalyser("test")
    a.addTempo(t1)
    a.addTempo(t2)
    a.addTempo(t3)
    a.addTempo(t4)
    val expected = 4
    val result = a.buffer.length
    assertResult(expected)(result)
  }

  "An WormAnalyser" should "remove any tempo duplicates" in {
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125.72726617, 120, None, System.currentTimeMillis())
    val t3 = Tempo(125.72726617, 120, None, System.currentTimeMillis())
    val t4 = Tempo(125.72726617, 120, None, System.currentTimeMillis())
    val a = WormAnalyser("test")
    a.addTempo(t1)
    a.addTempo(t2)
    a.addTempo(t3)
    a.addTempo(t4)
    val expected = List(t1, t2)
    val result = a.buffer
    println(a.buffer.toList)
    assertResult(expected)(result)
  }

}

class JSONParserTest extends FlatSpec {

  "A JSONParser" should "takes a class and produce a JSON string" in {
    val jp = JSONParser()
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125, 120, None, System.currentTimeMillis())
    val t3 = Tempo(135, 120, None, System.currentTimeMillis())
    val t4 = Tempo(145, 120, None, System.currentTimeMillis())
    val a = DWTAnalyser("test")
    a.addTempo(t1)
    a.addTempo(t2)
    a.addTempo(t3)
    a.addTempo(t4)
    val result = jp.write(a)
    assert(result != null)
  }

  "A JSONParser" should "write a json object to file" in {
    val jp = JSONParser()
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125, 120, None, System.currentTimeMillis())
    val t3 = Tempo(135, 120, None, System.currentTimeMillis())
    val t4 = Tempo(145, 120, None, System.currentTimeMillis())
    val a = WormAnalyser("test")
    val b = DWTAnalyser("test")
    val c = BeatrootAnalyser("test")
    a.addTempo(t1)
    a.addTempo(t2)
    a.addTempo(t3)
    a.addTempo(t4)
    b.addTempo(t1)
    b.addTempo(t2)
    b.addTempo(t3)
    b.addTempo(t4)
    c.addTempo(t1)
    c.addTempo(t2)
    c.addTempo(t3)
    c.addTempo(t4)
    val expected = jp.writeAll(a, b, c)
    jp.flushFull("/Users/philhannant/Desktop/Tempo.json")
    val source: String = Source.fromFile("/Users/philhannant/Desktop/Tempo.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val result = json.toString()
    assertResult(expected)(result)

  }

  "A JSONParser" should "write a wormanalyser object to a json file" in {
    val jp = JSONParser()
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125, 120, None, System.currentTimeMillis())
    val t3 = Tempo(135, 120, None, System.currentTimeMillis())
    val t4 = Tempo(145, 120, None, System.currentTimeMillis())
    val a = WormAnalyser("test")
    val b = DWTAnalyser("test")
    val c = BeatrootAnalyser("test")
    a.addTempo(t1)
    a.addTempo(t2)
    a.addTempo(t3)
    a.addTempo(t4)
    b.addTempo(t1)
    b.addTempo(t2)
    b.addTempo(t3)
    b.addTempo(t4)
    c.addTempo(t1)
    c.addTempo(t2)
    c.addTempo(t3)
    c.addTempo(t4)
    val expected = jp.writeAll(a, b, c)
    jp.flushFull("/Users/philhannant/Desktop/Tempo.json")
    val source: String = Source.fromFile("/Users/philhannant/Desktop/Tempo.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val result = json.toString()
    assertResult(expected)(result)

  }

  "A JSONParser" should "merge to json objects together and write to file" in {
    val jp2 = JSONParser()
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125, 120, None, System.currentTimeMillis())
    val t3 = Tempo(135, 120, None, System.currentTimeMillis())
    val t4 = Tempo(145, 120, None, System.currentTimeMillis())
    val a = WormAnalyser("test")
    val b = DWTAnalyser("test")
    val c = BeatrootAnalyser("test")
    a.addTempo(t1)
    a.addTempo(t2)
    a.addTempo(t3)
    a.addTempo(t4)
    b.addTempo(t1)
    b.addTempo(t2)
    b.addTempo(t3)
    b.addTempo(t4)
    c.addTempo(t1)
    c.addTempo(t2)
    c.addTempo(t3)
    c.addTempo(t4)
    val expectedNew = jp2.writeAll(a, b, c)
    jp2.flushFull("/Users/philhannant/Desktop/TempoNew.json")
    val source: String = Source.fromFile("/Users/philhannant/Desktop/TempoNew.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val result = json.toString()
    println(result)
    assertResult(expectedNew)(result)
  }

  "A JSONParser" should "take a stats object to" in {
    val jp = JSONParser()
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125, 120, None, System.currentTimeMillis())
    val s = Stats(1, 2, 3, 4, 4, 10)
    val a = WormAnalyser("test")
    val b = DWTAnalyser("test")
    val c = BeatrootAnalyser("test")
    a.addTempo(t1)
    a.addTempo(t2)
    b.addTempo(t1)
    b.addTempo(t2)
    c.addTempo(t1)
    c.addTempo(t2)
    a.stats = Some(s)
    b.stats = Some(s)
    c.stats = Some(s)
    val expected = jp.writeAll(a, b, c)
    jp.flushFull("/Users/philhannant/Desktop/TempoTest1.json")
    val source: String = Source.fromFile("/Users/philhannant/Desktop/TempoTest1.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val result = json.toString()
    assertResult(expected)(result)
  }

  "A JSONParser" should "be able to write just the Stats" in  {
    val jp = JSONParser()
    val w = WormAnalyser("wt")
    val d = DWTAnalyser("dt")
    val b = BeatrootAnalyser("bt")
    val s = StatsCalculator()
    val buff = new ListBuffer[Tempo]()
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125, 120, None, System.currentTimeMillis())
    val t3 = Tempo(135, 120, None, System.currentTimeMillis())
    val t4 = Tempo(145, 120, None, System.currentTimeMillis())
    buff += t1
    buff += t2
    buff += t3
    buff += t4
    w.stats = Some(Stats(s.getAverage(buff, "tempo"),
      s.getMedian(buff, "tempo"),
      s.getAverage(buff, "diffs"),
      s.getMedian(buff, "diffs"),
      s.getTotal(buff),
      s.getResponseTime(buff.toList)))
    d.stats = Some(Stats(s.getAverage(buff, "tempo"),
      s.getMedian(buff, "tempo"),
      s.getAverage(buff, "diffs"),
      s.getMedian(buff, "diffs"),
      s.getTotal(buff),
      s.getResponseTime(buff.toList)))
    b.stats = Some(Stats(s.getAverage(buff, "tempo"),
      s.getMedian(buff, "tempo"),
      s.getAverage(buff, "diffs"),
      s.getMedian(buff, "diffs"),
      s.getTotal(buff),
      s.getResponseTime(buff.toList)))
    jp.writeAll(w, d, b)
    val expected = jp.jsonStats
    jp.flushStats("/Users/philhannant/Desktop/StatsTest.json")
    val source: String = Source.fromFile("/Users/philhannant/Desktop/StatsTest.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val result = json.toString()
    println(expected)
    println(result)
    assertResult(expected)(result)


  }

}

  class StatsObjectTest extends FlatSpec {


    "A StatsCalculator" should "return a median of a listbuffer" in {
      val s = new StatsCalculator
      val buff = new ListBuffer[Tempo]()
      val t1 = Tempo(115, 120, None, System.currentTimeMillis())
      val t2 = Tempo(125, 120, None, System.currentTimeMillis())
      val t3 = Tempo(135, 120, None, System.currentTimeMillis())
      val t4 = Tempo(145, 120, None, System.currentTimeMillis())
      buff += t1
      buff += t2
      buff += t3
      buff += t4
      val result = s.getMedian(buff, "tempo")
      val expected = 130
      assertResult(expected)(result)
    }

    "A StatsCalculator" should "return a list of tempo values" in {
      val s = new StatsCalculator
      val buff = List[Tempo](Tempo(115, 120, None, System.currentTimeMillis()),
        Tempo(125, 120, None, System.currentTimeMillis()),
        Tempo(135, 120, None, System.currentTimeMillis()),
        Tempo(145, 120, None, System.currentTimeMillis()))
      val result = s.getTempos(buff)
      val expected = List[Double](115, 125, 135, 145)
      assertResult(expected)(result)
    }

    "A StatsCalculator" should "return a list if difference values" in {
      val s = new StatsCalculator
      val buff = List[Tempo](Tempo(115, 120, None, System.currentTimeMillis()),
        Tempo(125, 112, None, System.currentTimeMillis()),
        Tempo(135, 121, None, System.currentTimeMillis()),
        Tempo(145, 132, None, System.currentTimeMillis()))
      val result = s.getDiffs(buff)
      val expected = List[Double](120-115, 112-125, 121-135, 132-145)
      assertResult(expected)(result)
    }

    "A StatsCalculator" should "return an average" in {
      val s = new StatsCalculator
      val buff = new ListBuffer[Tempo]()
      val t1 = Tempo(115, 120, None, System.currentTimeMillis())
      val t2 = Tempo(125, 120, None, System.currentTimeMillis())
      val t3 = Tempo(135, 120, None, System.currentTimeMillis())
      val t4 = Tempo(145, 120, None, System.currentTimeMillis())
      buff += t1
      buff += t2
      buff += t3
      buff += t4
      val result = s.getAverage(buff, "tempo")
      val expected = (115+125+135+145)/4
      assertResult(expected)(result)
    }

    "A StatsCalculator" should "return the total beatCount" in {
      val s = new StatsCalculator
      val buff = new ListBuffer[Tempo]()
      val t1 = Tempo(115, 120, Some(4), System.currentTimeMillis())
      val t2 = Tempo(125, 120, Some(3), System.currentTimeMillis())
      val t3 = Tempo(135, 120, Some(5), System.currentTimeMillis())
      val t4 = Tempo(145, 120, Some(9), System.currentTimeMillis())
      buff += t1
      buff += t2
      buff += t3
      buff += t4
      val result = s.getTotal(buff)
      val expected = 9
      assertResult(expected)(result)
    }

    "A StatsCalculator" should "find the reponseTime" in {
      val s = new StatsCalculator
      val buff = new ListBuffer[Tempo]()
      val t1 = Tempo(115, 120, Some(4), 110)
      val t2 = Tempo(125, 120, Some(3), System.currentTimeMillis())
      val t3 = Tempo(119.5, 120, Some(5), 300)
      val t4 = Tempo(145, 120, Some(9), System.currentTimeMillis())
      buff += t1
      buff += t2
      buff += t3
      buff += t4
      val result = s.getResponseTime(buff.toList)
      val expected = 300
      assertResult(expected)(result)
    }
  }

class HtmlWriterTest extends FlatSpec{

  "An htmlWriter" should "write html" in {
    val ht = HtmlWriter
    val w = WormAnalyser("wt")
    val d = DWTAnalyser("dt")
    val b = BeatrootAnalyser("bt")
    val s = StatsCalculator()
    val buff = new ListBuffer[Tempo]()
    val t1 = Tempo(115, 120, None, System.currentTimeMillis())
    val t2 = Tempo(125, 120, None, System.currentTimeMillis())
    val t3 = Tempo(135, 120, None, System.currentTimeMillis())
    val t4 = Tempo(145, 120, None, System.currentTimeMillis())
    w.addTempo(t1)
    w.addTempo(t2)
    w.addTempo(t3)
    w.addTempo(t4)
    d.addTempo(t1)
    d.addTempo(t2)
    d.addTempo(t3)
    d.addTempo(t4)
    b.addTempo(t1)
    b.addTempo(t2)
    b.addTempo(t3)
    b.addTempo(t4)
    buff += t1
    buff += t2
    buff += t3
    buff += t4
    w.stats = Some(Stats(s.getAverage(buff, "tempo"),
      s.getMedian(buff, "tempo"),
      s.getAverage(buff, "diffs"),
      s.getMedian(buff, "diffs"),
      s.getTotal(buff),
      s.getResponseTime(buff.toList)))
    d.stats = Some(Stats(s.getAverage(buff, "tempo"),
      s.getMedian(buff, "tempo"),
      s.getAverage(buff, "diffs"),
      s.getMedian(buff, "diffs"),
      s.getTotal(buff),
      s.getResponseTime(buff.toList)))
    b.stats = Some(Stats(s.getAverage(buff, "tempo"),
      s.getMedian(buff, "tempo"),
      s.getAverage(buff, "diffs"),
      s.getMedian(buff, "diffs"),
      s.getTotal(buff),
      s.getResponseTime(buff.toList)))
    val expected = ht.writeHtml(List(w, d, b))
    ht.flush("/Users/philhannant/Desktop/HtmlTest.html")
    val result: String = Source.fromFile("/Users/philhannant/Desktop/HtmlTest.html").getLines.mkString
    assertResult(expected)(result)
  }

}