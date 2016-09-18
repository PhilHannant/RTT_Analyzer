name := "RTT_Analyser"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= List( "com.novocode" % "junit-interface" % "0.11" % Test,
  "org.scalafx" %% "scalafx" % "8.0.92-R10",
  "com.typesafe.akka" %% "akka-actor" % "2.4.8",
  "com.typesafe.play" %% "play-json" % "2.5.4",
  "org.scalatest" %% "scalatest" % "3.0.0-SNAP13" % "test",
  "org.apache.commons" % "commons-math3" % "3.6.1",
  "de.sciss" % "jwave" % "1.0.3")


unmanagedSourceDirectories in Compile += (baseDirectory / "lib/jwave/src").value

excludeFilter in unmanagedSources in Compile :=
  HiddenFileFilter || "*Test.java" || "*JWave.java"

fork in run := true

mainClass := Some("data.Operator")
