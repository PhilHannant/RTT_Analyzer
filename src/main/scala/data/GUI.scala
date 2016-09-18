package data

import java.awt.Desktop
import java.net.URL

import akka.actor.{ActorSystem, PoisonPill, Props}
import liveaudio.LiveAudioActor

import scalafx.Includes._
import scalafx.application.{JFXApp, Platform}
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.text.Text
import scalafx.scene.paint.Color._
import scalafx.stage.FileChooser

/**
  * @author Phil Hannant for MSc Computer Science project
  *
  * RTT_Analyser GUI object
  */


object GUI extends JFXApp {

  var state = false
  var htmlReady = false
  var beatCount: Double = 0.0
  var filePath = ""
  val startButton = new Button("Start")
  startButton.prefWidth = 100

  val stopButton = new Button("Stop")
  stopButton.prefWidth = 100

  val exitButton = new Button("Exit")
  exitButton.prefWidth = 100

  val testButton = new Button("Test")
  testButton.prefWidth = 100

  val expectedBpm = new TextField
  expectedBpm.promptText = "Expected BPM"
  expectedBpm.prefWidth = 250

  val fileNameLabel = new Label("")
  fileNameLabel.setTextFill(DarkGray)

  val fileChooser = new FileChooser()
  val getFilePath = new Button("Set File Name")
  getFilePath.prefWidth = 250

  val fileName = new TextField

  val openResults = new Button("Results")

  val settingsBox = new VBox
  settingsBox.id = "input"
  settingsBox.children = List(expectedBpm, getFilePath, fileNameLabel)
  settingsBox.prefWidth = 350

  val headingBox = new HBox
  headingBox.id = "heading"
  headingBox.padding = Insets(20)
  headingBox.children = Seq(
    new Text {
      text = "RTT_Analyser"
      style = "-fx-font-size: 36pt"
      fill = new LinearGradient(
        endX = 0,
        stops = Stops(DarkGray, Gray))
    }, settingsBox)

  val controlBar = new HBox(startButton,testButton, stopButton, openResults, exitButton)
  controlBar.prefWidth = 800
  controlBar.spacing = 25
  controlBar.id = "controlBar"

  val beatRootLabel = new Label("Beatroot")
  beatRootLabel.id = "tempo1"
  val beatRootTempo = new Label("0.00")
  beatRootTempo.id = "tempo1"
  val beatRootCount = new Label("0.00")
  beatRootCount.id = "tempo1"
  val dwtLabel = new Label("DWT")
  dwtLabel.id = "tempo2"
  val dwtTempo = new Label("0.00")
  dwtTempo.id = "tempo2"
  val wormLabel = new Label("AudioWorm")
  wormLabel.id = "tempo3"
  val wormTempo = new Label("0.00")
  wormTempo.id = "tempo3"

  val brBpm = new Label("bpm")
  brBpm.id = "tempo1"
  val dwBpm = new Label("bpm")
  dwBpm.id = "tempo2"
  val wBpm = new Label("bpm")
  wBpm.id = "tempo3"

  val gridPane = new GridPane
  gridPane.padding = Insets(0, 0, 0, 50)
  gridPane.hgap = 45
  gridPane.vgap = 35
  gridPane.add(beatRootLabel, 1, 1)
  gridPane.add(beatRootTempo, 2, 1)
  gridPane.add(brBpm, 4, 1)
  gridPane.add(dwtLabel, 1, 2)
  gridPane.add(dwtTempo, 2, 2)
  gridPane.add(dwBpm, 4, 2)
  gridPane.add(wormLabel, 1, 3)
  gridPane.add(wormTempo, 2, 3)
  gridPane.add(wBpm, 4, 3)

  val bPane = new BorderPane
  bPane.setBottom(controlBar)
  bPane.setTop(headingBox)
  bPane.setCenter(gridPane)

  /** Start button action event */
  startButton.onAction = (e: ActionEvent) => {
    start
  }

  /** Stop button action event */
  stopButton.onAction = (e: ActionEvent) => {
    if (!state) {
      error3.showAndWait()
    } else {
      Operator.opActors.liveAudioActor ! EndLiveAudio(Operator.opActors.processingActor)
      htmlReady = true
    }
  }
  /** Results button action event */
  openResults.onAction = (e: ActionEvent) => {
    if (!htmlReady) {
      error2.showAndWait()
    } else {
      val url = "file:" + """//""" + filePath + "stats.html"
      println(url)
      Desktop.getDesktop().browse(new URL(url).toURI())
    }
  }
  /** Exit button action event */
  exitButton.onAction = (e: ActionEvent) => {
    if (!state) System.exit(0)
    else Operator.opActors.liveAudioActor ! Close
  }

  /** Test button action event */
  testButton.onAction = (e: ActionEvent) => {
    if (filePath == "") {
      error.showAndWait()
    } else {
      start
      Operator.opActors.guiActor ! StartTestTimer(Operator.opActors.processingActor, Operator.opActors.liveAudioActor)
    }
  }
  /** Get file path action event */
  getFilePath.onAction = (e: ActionEvent) => {
    val path = fileChooser.showSaveDialog(stage)
    filePath = path.toString
    Platform.runLater{
      fileNameLabel.text = path.getName
    }
  }

  val error = new Alert(AlertType.Information)
  error.setTitle("Error Message")
  error.setContentText("Please input a file name")

  val error2 = new Alert(AlertType.Information)
  error2.setTitle("Error Message")
  error2.setContentText("HTML File Not Ready")

  val error3 = new Alert(AlertType.Information)
  error3.setTitle("Error Message")
  error3.setContentText("Please press Start or Test to proceed")

  /** set up of scalaFX scene */
  stage = new JFXApp.PrimaryStage {
    title = "RTT_Analyser"
    width = 800
    height = 500
    scene = new Scene {

      stylesheets += getClass.getResource("Styling.css").toExternalForm
      root = bPane
    }

  }
  /** Updates beatroot tempo value */
  def updatebrt(tempo: Double, count: Double) = {
    beatCount = beatCount + count
    Platform.runLater{
      beatRootTempo.text = f"$tempo%1.2f"
    }
  }
  /** Updates dwt tempo value */
  def updateDwt(tempo: Double) = {
    Platform.runLater{
      dwtTempo.text = f"$tempo%1.2f"
    }
  }
  /** Updates worm tempo value */
  def updateWorm(tempo: Double) = {
    Platform.runLater{
      wormTempo.text = f"$tempo%1.2f"
    }
  }

  /** Starts live audio capture and processing */
  def start = {
    if (expectedBpm.getText == "") expectedBpm.text = "0"
    if (filePath == "") {
      error.showAndWait()
    } else {
      if (!state){
        Operator.setup
        state = true
      } else {
        Operator.reset
        Operator.setup
      }
      Operator.opActors.liveAudioActor ! StartLiveAudio(expectedBpm.getText.toDouble, Operator.opActors.processingActor, filePath, System.currentTimeMillis())
    }
  }

}


/**
  * Operator object acts as main class for RTT_Analyser
  */

object Operator extends App {

  var opActors: OperatorActors = _
  var time: Long = _

  /** instantiate actors */
  def setup = {
    opActors = new OperatorActors
    time = System.currentTimeMillis()
  }

  /** Kills actors */
  def reset = {
    opActors.liveAudioActor ! PoisonPill
    opActors.beatrootActor ! PoisonPill
    opActors.dwtActor ! PoisonPill
    opActors.processingActor ! PoisonPill
    opActors.guiActor ! PoisonPill
  }

  val gui = GUI
  gui.main(args: Array[String])



}


/**
  * OperatorActors allows for resetting of RTT_Analyser actors for multiple tests
  */

class OperatorActors {

  val system = ActorSystem("liveAudioSystem")
  val liveAudioActor = system.actorOf(Props[LiveAudioActor], "liveAudioActor")
  val beatrootActor = system.actorOf(Props[BeatrootActor], "beatrootActor")
  val dwtActor = system.actorOf(Props[DwtActor], "dwtActor")
  val processingActor = system.actorOf(Props(new ProcessingActor(beatrootActor, dwtActor)))
  val guiActor = system.actorOf(Props[GUIActor], "guiActor")

}