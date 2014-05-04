
import akka.actor.ActorRef
import java.awt.Color
import scala.swing._
import scala.swing.event.ButtonClicked
import scala.swing.event.EditDone

object Gui {

  class PropertyRow(model: ActorRef, propertyName: String, propertyValue: String) extends FlowPanel {
    val valueLabel = new TextField(propertyValue)
    listenTo(valueLabel)

    contents += new GridPanel(1, 2) {
      contents += new Label("Name: " + propertyName)
      contents += valueLabel
    }

    border = Swing.LineBorder(Color.BLACK)

    import ModelActor._
    reactions += {

      case EditDone(x) =>
        println("TextField Edit Done class " + x.getClass)

        println("TextField Edit Done " + propertyName + ", [" + x.text + "]")
        model ! UpdateProperty(propertyName, x.text)
    }

    def clearValue() = valueLabel.text = "cleared"
    def setValue(s: String) = valueLabel.text = s
  }

/*
  def createRowMap(model: ActorRef): Map[String, PropertyRow] = {
    val names = List("A", "B", "C", "Dog")

    val rows = for {
      name <- names
    } yield name -> new PropertyRow(model, name)

    rows.toMap
  }
  */
}

class Gui(modelActor: ActorRef) extends MainFrame {
  import Gui._
  import ModelActor._

  title = "Config-Akka"
  resizable = true

  // topPanel contains editable rows

  val topPanel = new BoxPanel(Orientation.Vertical)
  var rows = Map[String, PropertyRow]()

  // Bottom panel has buttons

  val clearButton = new Button("Clear")
  val saveButton = new Button("Save")
  val stopButton = new Button("Stop")

  val bottomPanel = new FlowPanel() {
    contents += clearButton
    contents += saveButton
    contents += stopButton
  }

  // Main panel
  val mainPanel = new BoxPanel(Orientation.Vertical) {
    contents += topPanel
    contents += bottomPanel
  }

  contents = mainPanel

  listenTo(clearButton)
  listenTo(saveButton)
  listenTo(stopButton)

  reactions += {
    case ButtonClicked(`clearButton`) =>
      modelActor ! ClearProperties

    case ButtonClicked(`saveButton`) =>
      modelActor ! Save("/tmp/foo.properties")

    case ButtonClicked(`stopButton`) =>
      //masterActor ! Stop
      System.exit(0)
  }

  def addRow(name: String, value: String) = {
    val row = new PropertyRow(modelActor, name, value)
    rows = rows.updated(name, row)
    topPanel.contents += row
    //mainPanel.revalidate()
    pack()
  }


  def clear() = rows.foreach(_._2.clearValue())

  def setProperties(properties: Map[String, String]) = {
    println(s"Gui.setProperties: $properties")
    properties.foreach(kv => addRow(kv._1, kv._2))
  }
  def setProperty(name: String, value: String) = {
    rows(name).setValue(value)
    rows(name).repaint()
  }

}

