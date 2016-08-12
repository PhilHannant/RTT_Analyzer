package data

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label}
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
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
    width = 600
    height = 450
    scene = new Scene {

      val enterBpm = new Label("Enter expected BPM")
      enterBpm.layoutX = 200
      enterBpm.layoutY = 100

      

      content = Set(new Button("Start") {
        layoutX = 300
        layoutY = 225
      }, new HBox {
        padding = Insets(20)
        children = Seq(
          new Text {
            text = "RTT_ANalyser "
            style = "-fx-font-size: 36pt"
            fill = new LinearGradient(
              endX = 0,
              stops = Stops(DarkGray, Gray))
          }
        )
      }
      )
    }
  }
}
