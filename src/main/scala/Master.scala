import akka.actor.{Actor, Props}
import com.typesafe.config._
import scala.collection.JavaConverters._

object Master {
  case class Stop()

  def props(): Props = Props(new Master())
}

class Master extends Actor {
  import Master._
  import ModelActor._

  override def preStart() = {
    val conf = ConfigFactory.load()
    val defaults = conf.getObject("default-properties").unwrapped().asScala.mapValues(_.toString).toMap

    val modelActor = context.actorOf(ModelActor.props(defaults), "MODEL")

    val guiActor1 = context.actorOf(GuiActor.props(modelActor), "GUI1")
    guiActor1 ! GuiActor.Show

    //val guiActor2 = context.actorOf(GuiActor.props(modelActor), "GUI2")
    //guiActor2 ! GuiActor.Show

    modelActor ! RegisterGui(guiActor1)
    //modelActor ! RegisterGui(guiActor2)

    modelActor ! Load("/tmp/foo.properties")
  }

  def receive = {
    case Stop => println("Master.Stop")
  }


}
