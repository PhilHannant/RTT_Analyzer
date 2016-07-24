package dwtbpm



import java.io.{BufferedInputStream, File, FileInputStream}
import java.nio.file.{Files, Paths}

import scala.collection.mutable.ArrayBuffer
import scala.math.min
import scala.math.pow
import scala.math.abs
import jwave.transforms._
import jwave._
import jwave.transforms.wavelets.daubechies._
import jwave.transforms.wavelets.haar._
import liveaudio._
import ArrayOperations._

import scala.collection.mutable

/**
  * Adapted from dwt bpm detector created by Mario Ziccardi, source https://github.com/mziccard/scala-audio-file,
  * licence details below
  *
  * Copyright (c) 2015 Marco Ziccardi
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in
  * all copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  * THE SOFTWARE.
  *
  * Class <code>WaveletBPMDetector</code> can be used to
  * detect the tempo of a track in beats-per-minute.
  * The class implements the algorithm presented by
  * Tzanetakis, Essl and Cookin the paper titled
  * "Audio Analysis using the Discrete Wavelet Transform"
  *
  * Objects of the class can be created using the companion
  * object's factory method.
  *
  * To detect the tempo the discrete wavelet transform is used.
  * Track samples are divided into windows of frames.
  * For each window data are divided into 4 frequency sub-bands
  * through DWT. For each frequency sub-band an envelope is
  * estracted from the detail coffecients by:
  * 1) Full wave rectification (take the absolute value),
  * 2) Downsampling of the coefficients,
  * 3) Normalization (via mean removal)
  * These 4 sub-band envelopes are then summed together.
  * The resulting collection of data is then autocorrelated.
  * Peaks in the correlated data correspond to peaks in the
  * original signal.
  * then peaks are identified on the filtered data.
  * Given the position of such a peak the approximated
  * tempo of the window is computed and appended to a colletion.
  * Once all windows in the track are processed the beat-per-minute
  * value is returned as the median of the windows values.
  *
  * Audio track data is buffered so that there's no need
  * to load the whole track in memory before applying
  * the detection.
  *
  * Class constructor is private, use the companion
  * object instead.
  **/
class WaveletBPMDetector (
                                   val liveAudio: SoundCaptureImpl,
                                   val audioProcessor: LiveAudioProcessor,
                                   val windowFrames : Int,
                                   val waveletType : WaveletBPMDetector.Wavelet
                                   ) extends BPMDetector {
//  val windowsToProcess : Int

  val wavelet = waveletType match {
    case WaveletBPMDetector.Haar => new Haar1()
    case WaveletBPMDetector.Daubechies4 => new Daubechies4()
  }

  /*
   * Array of BPM values computed for each window in the track.
   * Overall BPM is computed as the median of this collection
   **/
  private var instantBpm = ArrayBuffer[Double]()


  /**
    * The tempo in beats-per-minute computed for the track
    **/
  var _bpm : Double = -1.0;

  /**
    * Identifies the location of data with the maximum absolute
    * value (either positive or negative). If multiple data
    * have the same absolute value the last positive is taken
    * @param data the input array from which to identify the maximum
    * @return the index of the maximum value in the array
    **/
  private def detectPeak(data : Array[Double]) : Int = {
    var max: Double = Double.MinValue

    for (x <- data) {
      if (abs(x) > max) max = abs(x)
    }
    var location = -1;
    var i = 0
    while (i < data.length && location == -1) {
      if (data(i) == max) {
        location = i;
      }
      i = i + 1;
    }
    i = 0;
    while (i < data.length && location == -1) {
      if (data(i) == -max) {
        location = i;
      }
      i = i + 1;
    }
    return location
  }

  /**
    * Given <code>windowFrames</code> samples computes a BPM
    * value for the window and pushes it in <code>instantBpm</code>
    * @param an array of <code>windowFrames</code> samples representing the window
    **/
  private def computeWindowBpm(data : Array[Double]) {
    var aC : Array[Double] = null
    var dC : Array[Double] = null
    var dCSum : Array[Double] = null
    var dCMinLength : Int = 0
    val levels = 4
    val maxDecimation = pow(2, levels-1)
    val minIndex : Int = (60.toDouble / 220 *  liveAudio.sampleRate.toDouble/maxDecimation).toInt
    val maxIndex : Int = (60.toDouble / 40 * liveAudio.sampleRate.toDouble/maxDecimation).toInt

    // 4 Level DWT
    for (loop <- 0 until levels) {

      // Apply DWT
      val transform = new Transform(new FastWaveletTransform(wavelet));
      if (loop == 0) {
        val coefficients : Array[Array[Double]] = transform.decompose(data)
        val l = coefficients.length - 1
        aC = coefficients(1).slice(0, coefficients(1).length/2)
        dC = coefficients(l).slice(coefficients(l).length/2, coefficients(l).length)
        dCMinLength = (dC.length/maxDecimation).toInt + 1
      } else {
        val coefficients : Array[Array[Double]] = transform.decompose(aC)
        val l = coefficients.length - 1
        aC = coefficients(1).slice(0, coefficients(1).length/2)
        dC = coefficients(l).slice(coefficients(l).length/2, coefficients(l).length)
      }

      // Extract envelope from detail coefficients
      //  1) Undersample
      //  2) Absolute value
      //  3) Subtract mean
      val pace = pow(2, (levels-loop-1)).toInt
      dC = dC.undersample(pace).abs
      dC = dC - dC.mean

      // Recombine detail coeffients
      if (dCSum == null) {
        dCSum = dC.slice(0, dCMinLength)
      } else {
        dCSum = dC.slice(0, min(dCMinLength, dC.length)) |+| dCSum
      }
    }

    // Add the last approximated data
    aC = aC.abs
    aC = aC - aC.mean
    dCSum = aC.slice(0, min(dCMinLength, dC.length)) |+| dCSum

    // Autocorrelation
    var correlated : Array[Double] = dCSum.correlate
    val correlatedTmp = correlated.slice(minIndex, maxIndex)


    // Detect peak in correlated data
    val location = detectPeak(correlatedTmp)

    // Compute window BPM given the peak
    val realLocation = minIndex + location
    val windowBpm : Double = 60.toDouble / realLocation * (liveAudio.sampleRate.toDouble/maxDecimation)
    println("windownbpm " + windowBpm)
    instantBpm += windowBpm
  }

  override def bpm() : Double = {
    var count = 0
    if (_bpm == -1) {
//      for (currentWindow <- 0 until windowsToProcess) {
        val buffer : Array[Int]  = new Array[Int](windowFrames * liveAudio.channels)
        val framesRead = audioProcessor.readFrames(buffer, windowFrames)
        val leftChannelSamples : Array[Double] =
          buffer.zipWithIndex.filter(_._2 % 2 == 0).map(_._1.toDouble)
        computeWindowBpm(leftChannelSamples)
//      }
      println("out of loop")
      _bpm = instantBpm(0)//.toArray.median
    }
    println("at return" + _bpm)
    _bpm
  }


}

object WaveletBPMDetector {

  /**
    * Trait for wavelet types
    **/
  sealed trait Wavelet

  /**
    * Haar wavelet type
    **/
  case object Haar extends Wavelet

  /**
    * Daubechies4 wavelet type
    **/
  case object Daubechies4 extends Wavelet

  class WindowSizeException(message: String = null, cause: Throwable = null)
    extends RuntimeException(message, cause);

  /**
    * Create a <code>WaveletBPMDetector</code> object given an audio file,
    * a size of the window in frames and a wavelet type
    * @param audioFile Audio file
    * @param windowFrames Number of frames that form a window on which the beat detection
    * algorithm is applied. Due to the way DWT is applied <code>windowFrames</code>
    * must be a power of two. <code>windowFrames</code> should be such that a window
    * corresponds to 1 to 10 seconds of the audio file.
    * @param waveletType The type of wavelet. Default value is <code>Daubechies4</code>
    * @param windowsToConsider The number of windows to process. If not specified
    * the whole track is processed
    * @return A object of the class <code>WaveletBPMDetector</code>
    * @throws WindowSizeException
    **/
  def apply(
             liveAudio : SoundCaptureImpl,
             audioProcessor: LiveAudioProcessor,
             windowFrames : Int,
             waveletType : Wavelet = Daubechies4,
             windowsToConsider : Int = 0) : WaveletBPMDetector = {

    if ((windowFrames < 0) || !((windowFrames & (windowFrames - 1)) == 0))
      throw new WindowSizeException("windowFrames parameter must be a power of 2")

    var windowsNumber = windowsToConsider
    println("windowns " + windowsToConsider)
    if (windowsNumber <= 0) {
      windowsNumber = (2097152 / windowFrames)
    }


    return new WaveletBPMDetector(liveAudio, audioProcessor, windowFrames, waveletType )//windowsNumber
  }

}


object test extends App {
  val liveAudio = new SoundCaptureImpl
  val audioProcessor = new LiveAudioProcessor
  val tempo = WaveletBPMDetector(
    liveAudio,
    audioProcessor,
    131072,
    WaveletBPMDetector.Daubechies4).bpm()
  println(tempo)
}
