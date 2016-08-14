package data

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
import scalafx.scene.layout.{BorderPane, GridPane, HBox}
import scalafx.scene.paint.{Color, LinearGradient, Stops}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.scene.paint.Color._

/**
  * Created by philhannant on 11/08/2016.
  */


object GUI extends JFXApp {

    stage = new JFXApp.PrimaryStage {
      title = "RTT_Analyser"
      width = 800
      height = 500
      scene = new Scene {

        stylesheets += getClass.getResource("Styling.css").toExternalForm

        fill = Black
        val headingBox = new HBox
        headingBox.padding = Insets(20)
        headingBox.children = Seq(
          new Text {
            text = "RTT_Analyser "
            style = "-fx-font-size: 36pt"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(DarkGray, Gray))
          })


        val startButton = new Button("Start")
        startButton.prefWidth = 100

        val stopButton = new Button("Stop")
        stopButton.prefWidth = 100

        val enterBpm = new Label("Expected BPM")
        enterBpm.setTextFill(DarkGray)
        enterBpm.layoutX = 200
        enterBpm.layoutY = 100

        val fileName = new Label("File Name")
        fileName.setTextFill(DarkGray)
        fileName.layoutX = 200
        fileName.layoutY = 100

//        val controlBar = new ButtonBar {id = "buttonBar"; buttons = List(startButton, stopButton)}
//        controlBar.layoutX = 300
//        controlBar.layoutY = 425
//        controlBar.minWidth = 400

        val controlBar = new HBox(startButton, stopButton)
        controlBar.prefWidth = 800
        controlBar.spacing = 25
        controlBar.id = "controlBar"


        val bPane = new BorderPane
        bPane.setBottom(controlBar)
        bPane.setTop(headingBox)




        val expectedBpm = new TextField
        expectedBpm.layoutX = 300
        expectedBpm.layoutY = 100
        expectedBpm.promptText = "BPM?"


        //content = List(headingBox, enterBpm, expectedBpm, bPane)

        root = bPane

        startButton.onAction = (e: ActionEvent) => {
          //will need to call start in actor
          Operator.liveAudioActor ! StartLiveAudio(expectedBpm.getText.toDouble, Operator.processingActor)
        }

        stopButton.onAction = (e: ActionEvent) => {
          //will need to call start in actor
          Operator.liveAudioActor ! EndLiveAudio
        }

      }

    }

}

object Operator extends App{


  val system = ActorSystem("liveAudioSystem")
  val liveAudioActor = system.actorOf(Props[LiveAudioActor], "liveAudioActor")
  val processingActor = system.actorOf(Props[ProcessingActor], "processor")

  val gui = GUI
  gui.main(args: Array[String])





}