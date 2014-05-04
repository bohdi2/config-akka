
import akka.actor.ActorRef
import java.awt.Color
import scala.swing._
import scala.swing.event.ButtonClicked
import scala.swing.event.EditDone

object Gui {

  class PropertyRow(model: ActorRef, propertyName: String) extends FlowPanel {
    val valueLabel = new TextField(propertyName)
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


  def createRowMap(model: ActorRef): Map[String, PropertyRow] = {
    val names = List("A", "B", "C", "Dog")

    val rows = for {
      name <- names
    } yield name -> new PropertyRow(model, name)

    rows.toMap
  }
}

class Gui(modelActor: ActorRef) extends MainFrame {
  import Gui._
  import ModelActor._

  title = "Config-Akka"

  // topPanel contains editable rows

  val topPanel = new BoxPanel(Orientation.Vertical)
  val rows = createRowMap(modelActor)
  
  rows.foreach(p => topPanel.contents += p._2)

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

  contents = new BoxPanel(Orientation.Vertical) {
    contents += topPanel
    contents += bottomPanel
  }

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

  def clear() = rows.foreach(_._2.clearValue())

  def setProperty(name: String, value: String) = {
    rows(name).setValue(value)
    rows(name).repaint()
  }

}

