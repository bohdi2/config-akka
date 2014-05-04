import akka.actor.{Actor, Props}

object Master {
  case class Stop()

  def props(): Props = Props(new Master())
}

class Master extends Actor {
  import Master._
  import ModelActor._

  override def preStart() = {

    val modelActor = context.actorOf(ModelActor.props(), "MODEL")

    val guiActor1 = context.actorOf(GuiActor.props(modelActor), "GUI1")
    guiActor1 ! GuiActor.Show

    val guiActor2 = context.actorOf(GuiActor.props(modelActor), "GUI2")
    guiActor2 ! GuiActor.Show

    modelActor ! RegisterGui(guiActor1)
    modelActor ! RegisterGui(guiActor2)
  }

  def receive = {
    case Stop => println("Master.Stop")
  }


}
