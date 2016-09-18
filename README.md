# RTT_Analyzer

Small software project developed as part of my MSc Computer Science Project.

The RTT\_Analyser implements an adapted version of the beat tracking system Beatroot by Simon Dixon and the DWT algorithm described by Tzanetakis et al. Also included is the Performance Worm system created by Simon Dixon, Werner Goebl and Gerhard Widmer.

The DWT algorithm is an adapted version of the implementation created by Marco Ziccard, https://github.com/mziccard/scala-audio-file

The RTT\_Analyser uses an Akka Actor system to pass the captured live audio around for processing.

To run is sbt select option 2 when prompted.
