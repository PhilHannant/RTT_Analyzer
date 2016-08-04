name := "RTT_Analyzer"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test


unmanagedSourceDirectories in Compile += (baseDirectory / "lib/jwave/src").value

excludeFilter in unmanagedSources in Compile :=
  HiddenFileFilter || "*Test.java" || "*JWave.java"

fork in run := true
