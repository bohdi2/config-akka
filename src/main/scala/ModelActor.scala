
import akka.actor.{ActorRef, Actor, Props}
import java.io.FileWriter
import java.util.Properties
import scala.collection.mutable.ArrayBuffer

object ModelActor {
  //case class Stop()
  case class Save(filename: String)
  case class ClearProperties()
  case class UpdateProperty(name: String, value: String)
  case class DeregisterGui(guiActor: ActorRef)
  case class RegisterGui(guiActor: ActorRef)

  def props(): Props = Props(new ModelActor())
}

class ModelActor() extends Actor {
  import ModelActor._
  import GuiActor._

  private val properties = new Properties
  private val views = ArrayBuffer.empty[ActorRef]

   def receive = {
    case RegisterGui(guiActor) =>
      println("ModelActor.register")
      views += guiActor

      // Need to send the gui our data to display
      //guiActor ! Data(properties.)

    case DeregisterGui(guiActor) =>
      println("ModelActor.deregister")
      views -= guiActor

    case Save(filename) =>
      println("ModelActor.save: " + filename)
      properties.store(new FileWriter(filename), "Model")

    case ClearProperties =>
      println("ModelActor.clear")
      properties.clear()
     views.map(_ ! Clear)

    case UpdateProperty(name, value) =>
     println("ModelActor.update " + name + " [" + value + "]")
     properties.setProperty(name, value)
     views.map(_ ! DisplayProperty(name, value))

  }


}
