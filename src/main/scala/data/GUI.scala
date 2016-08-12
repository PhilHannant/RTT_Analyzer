package data

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

/**
  * Created by philhannant on 11/08/2016.
  */


object GUI extends JFXApp {

  stage = new JFXApp.PrimaryStage {
    title = "RTT_Analyser"
    width = 600
    height = 450
    scene = new Scene {
      content = Set(new Button("Start") {

      })
    }
  }
}
