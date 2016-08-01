
import dwtbpm.WaveletBPMDetector
import org.scalatest._
import liveaudio._
import at.ofai.music.worm._
import data.JSONParser

import scala.collection.mutable.ArrayBuffer
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
    val t = Tempo(120, 120)
    val result = t.tempo
    val expected = 120
    assertResult(expected)(result)
  }

  "A Tempo object" should "calculate the difference between 2 tempos" in {
    val t = Tempo(119.983827, 120)
    val result = t.difference
    val expected = 120 - 119.983827
    assertResult(expected)(result)
  }

  "An Analyzer" should "take multiple tempo objects" in {
    val t1 = Tempo(115, 120)
    val t2 = Tempo(125, 120)
    val t3 = Tempo(135, 120)
    val t4 = Tempo(145, 120)
    val buffer = new ArrayBuffer[Tempo]
    val a = DWTAnalyser("test", buffer)
    a.addTempo(t1)
    a.addTempo(t2)
    a.addTempo(t3)
    a.addTempo(t4)
    val expected = 4
    val result = a.buffer.length
    assertResult(expected)(result)
  }

}

class JSONParserTest extends FlatSpec {

  "A JSONParser" should "takes a class and produce a JSON string" in {
    val jp = JSONParser()
    val t1 = Tempo(115, 120)
    val t2 = Tempo(125, 120)
    val t3 = Tempo(135, 120)
    val t4 = Tempo(145, 120)
    val buffer = new ArrayBuffer[Tempo]
    val a = DWTAnalyser("test", buffer)
    a.addTempo(t1)
    a.addTempo(t2)
    a.addTempo(t3)
    a.addTempo(t4)
    val result = jp.write(a)
    assert(result != null)




  }



}
