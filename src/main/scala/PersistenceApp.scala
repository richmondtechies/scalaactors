import akka.persistence._
import akka.actor.{ Actor, ActorRef, ActorSystem, Props }


object PersistentApp extends App {
  import RetrieveBalance._

  val system = ActorSystem("persistent-actors")

  val counter = system.actorOf(Props[RetrieveBalance])

    counter ! Cmd(Increment(3))

  counter ! Cmd(Increment(5))

  counter ! Cmd(Decrement(3))

  counter ! "print"

  Thread.sleep(1000)

  system.terminate()

}






