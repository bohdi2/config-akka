
import akka.actor.{Props, Actor, ActorRef}

object GuiActor {
  case object Clear
  case object Show
  case class RegisterModel(modelActor: ActorRef)
  case class UpdateNameValue(name: String, value: String)
  case class DisplayProperties(properties: Map[String, String])
  case class DisplayProperty(name: String, value: String)

  def props(modelActor: ActorRef): Props = Props(new GuiActor(modelActor))
}

class GuiActor(modelActor: ActorRef) extends Actor {
  import GuiActor._

  private val gui = new Gui(modelActor)

  def receive = {
    case Show => gui.visible = true

    case Clear =>
      println("GuiActor.received clear")
      gui.clear()

    case DisplayProperties(properties) =>
      println(s"GuiActor DisplayProperties $properties")
      gui.setProperties(properties)

    case DisplayProperty(name, value) =>
      println(s"GuiActor UpdateNameValue $name -> $value")
      gui.setProperty(name, value)
  }
}