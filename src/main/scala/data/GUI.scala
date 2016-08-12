package data

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

/**
  * Created by philhannant on 11/08/2016.
  */


object GUI extends JFXApp {

  stage = new JFXApp.PrimaryStage {
    title = "Hello World"
    width = 600
    height = 450
    scene = new Scene {
      fill = Color.LIGHTGREEN
      content = Set(new Rectangle {
        x = 25
        y = 40
        width = 100
        height = 100
        fill <== when(hover) choose Color.GREEN otherwise Color.RED
      })
    }
  }
}
