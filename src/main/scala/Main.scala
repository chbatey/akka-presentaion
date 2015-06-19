import akka.actor._

object Main extends App {
  override def main(args: Array[String]): Unit = {
    println("Hello World")

    val system = ActorSystem("HelloWorld")
    val server = system.actorOf(Props(classOf[Server]))
    val client = system.actorOf(Props(classOf[Client], server))
    val supervisor = system.actorOf(Props(classOf[Supervisor], Set(server, client)))
    client ! "go"
  }
}

class Supervisor(list: Set[ActorRef]) extends Actor {
  var children = list

  children.foreach(context.watch)

  def receive: Receive = {
    case Terminated(actorRef) =>
      println(s"Actor shutdown $actorRef")
      children = children - actorRef
      if (children.isEmpty) context.system.shutdown()
  }
}

class Client(server: ActorRef) extends Actor {
  def receive: Receive = {
    case "go" =>
      println("pinging the server")
      server ! "ping"
    case "pong" =>
      println("Client stopping")
      context.stop(self)
  }
}

class Server extends Actor {
  def receive: Receive = {
    case "ping" =>
      println("ponging the client")
      sender() ! "pong"
      println("Server stopping")
      context.stop(self)
  }
}
