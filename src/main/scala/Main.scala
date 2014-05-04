
import akka.actor._

object  Main extends App {

  val actorSystem = ActorSystem("EBS")

  actorSystem.actorOf(Master.props())

  //println("Main asking master to start")
  //master ! Master.Start

}
