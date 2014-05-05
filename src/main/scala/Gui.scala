
import akka.actor.ActorRef
import java.awt.Color
import scala.swing._
import scala.swing.event.EditDone
import swing.event.{TableRowsSelected, TableColumnsSelected, ButtonClicked}


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

  val tmodel = Array(List("Mary", "Campione", "Snowboarding", 5, false).toArray,
    List("Alison", "Huml", "Rowing", 5, false).toArray,
    List("Kathy", "Walrath", "Knitting", 5, false).toArray,
    List("Sharon", "Zakhour", "Speed reading", 5, false).toArray,
    List("Philip", "Milne", "Pool", 5, true).toArray)

  val table = new Table(tmodel, Array("First Name", "Last Name", "Sport", "# of Years", "Vegetarian")) {
    preferredViewportSize = new Dimension(500, 70)
  }
  table.selection.intervalMode = Table.IntervalMode.Single
  table.selection.elementMode = Table.ElementMode.Cell

  listenTo(table.selection)
  //contents += new ScrollPane(table)

  // Main panel
  val mainPanel = new BoxPanel(Orientation.Vertical) {
    contents += new ScrollPane(table)
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

    case TableRowsSelected(t, range, b) =>
      println(s"Rows selected, range: $range, flag: $b")
      //println("rows t: " + t)

    case TableColumnsSelected(t, range, b) =>
      println(s"Columns selected, range: $range, flag: $b")
      //println("rows x: " + x)


    case x => println("Unknown: " + x.getClass())
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

