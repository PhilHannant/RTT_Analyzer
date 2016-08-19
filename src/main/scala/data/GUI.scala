package data

import java.awt.Desktop
import java.net.URL

import akka.actor.{ActorSystem, Props}
import liveaudio.LiveAudioActor

import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout._
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.scene.paint.Color._
import scalafx.stage.FileChooser

/**
  * Created by philhannant on 11/08/2016.
  */


object GUI extends JFXApp {

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

//  val enterBpm = new Label("")
//  enterBpm.setTextFill(DarkGray)
  //        enterBpm.layoutX = 200
  //        enterBpm.layoutY = 100

  val expectedBpm = new TextField
  //        expectedBpm.layoutX = 300
  //        expectedBpm.layoutY = 100
  expectedBpm.promptText = "Expected BPM"
  expectedBpm.prefWidth = 250

  val fileNameLabel = new Label("")
  fileNameLabel.setTextFill(DarkGray)
  //        fileName.layoutX = 200
  //        fileName.layoutY = 100
  val fileChooser = new FileChooser()
  val getFilePath = new Button("Set File Name")
  getFilePath.prefWidth = 250

  val fileName = new TextField

  //        val controlBar = new ButtonBar {id = "buttonBar"; buttons = List(startButton, stopButton)}
  //        controlBar.layoutX = 300
  //        controlBar.layoutY = 425
  //        controlBar.minWidth = 400

  val openResults = new Button("Results")



  val settingsBox = new VBox
  settingsBox.id = "input"
  settingsBox.children = List(expectedBpm, getFilePath, fileNameLabel)


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
  beatRootLabel.id = "tempo"
  val beatRootTempo = new Label("0.00")
  beatRootTempo.id = "tempo"
  val beatRootCount = new Label("0.00")
  beatRootCount.id = "tempo"
  val dwtLabel = new Label("DWT")
  dwtLabel.id = "tempo"
  val dwtTempo = new Label("0.00")
  dwtTempo.id = "tempo"
  val wormLabel = new Label("AudioWorm")
  wormLabel.id = "tempo"
  val wormTempo = new Label("0.00")
  wormTempo.id = "tempo"

 // val pb = new ProgressBar()
 // pb.setProgress(-1.0)



  val gridPane = new GridPane
  gridPane.padding = Insets(0, 0, 0, 50)
  gridPane.hgap = 10
  gridPane.vgap = 35
 // gridPane.add(enterBpm, 1, 0)
//  gridPane.add(fileNameLabel, 4, 0)
 // gridPane.add(expectedBpm, 2, 0)
//  gridPane.add(getFilePath, 5, 0)
  gridPane.add(beatRootLabel, 1, 1)
  gridPane.add(beatRootTempo, 2, 1)
  gridPane.add(beatRootCount, 3, 1)
  gridPane.add(dwtLabel, 1, 2)
  gridPane.add(dwtTempo, 2, 2)
  gridPane.add(wormLabel, 1, 3)
  gridPane.add(wormTempo, 2, 3)
  //gridPane.add(pb, 3, 4)

  val bPane = new BorderPane
  bPane.setBottom(controlBar)
  bPane.setTop(headingBox)
  bPane.setCenter(gridPane)

  //content = List(headingBox, enterBpm, expectedBpm, bPane)


  startButton.onAction = (e: ActionEvent) => {
    start
  }

  stopButton.onAction = (e: ActionEvent) => {
    //will need to call start in actor
    Operator.liveAudioActor ! EndLiveAudio(Operator.processingActor)
  }

  openResults.onAction = (e: ActionEvent) => {
    Desktop.getDesktop().browse(new URL("file:///Users/philhannant/Desktop/HtmlTest.html").toURI());
  }

  exitButton.onAction = (e: ActionEvent) => {
    Operator.liveAudioActor ! Close
  }

  testButton.onAction = (e: ActionEvent) => {
    start
    Operator.guiActor ! StartTestTimer(Operator.processingActor, Operator.liveAudioActor)
  }

  getFilePath.onAction = (e: ActionEvent) => {
    val path = fileChooser.showSaveDialog(stage)
    filePath = path.toString
    Platform.runLater{
      fileNameLabel.text = filePath
    }
  }

  stage = new JFXApp.PrimaryStage {
    title = "RTT_Analyser"
    width = 800
    height = 500
    scene = new Scene {

      stylesheets += getClass.getResource("Styling.css").toExternalForm
      root = bPane
    }

  }

  def updatebrt(tempo: Double, count: Double) = {
    beatCount = beatCount + count
    Platform.runLater{
      beatRootTempo.text = f"$tempo%1.2f"
      beatRootCount.text = f"$beatCount%1.2f"
    }
  }

  def updateDwt(tempo: Double) = {
    Platform.runLater{
      dwtTempo.text = f"$tempo%1.2f"
    }
  }

  def updateWorm(tempo: Double) = {
    Platform.runLater{
      wormTempo.text = f"$tempo%1.2f"
    }
  }


  def start = {
    if (expectedBpm.getText == "") expectedBpm.text = "0"
    Operator.liveAudioActor ! StartLiveAudio(expectedBpm.getText.toDouble, Operator.processingActor)
  }

}

object Operator extends App {


  val system = ActorSystem("liveAudioSystem")
  val liveAudioActor = system.actorOf(Props[LiveAudioActor], "liveAudioActor")
  val beatrootActor = system.actorOf(Props[BeatrootActor], "beatrootActor")
  val dwtActor = system.actorOf(Props[DwtActor], "dwtActor")
  val processingActor = system.actorOf(Props(new ProcessingActor(beatrootActor, dwtActor)))
  val guiActor = system.actorOf(Props[GUIActor], "guiActor")

  val gui = GUI
  gui.main(args: Array[String])



}