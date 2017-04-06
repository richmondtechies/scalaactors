import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by tki214 on 4/5/17.
  */


object TVMessage {
  case object StartTVMsg
  case object StopTVMsg
}

class TVActor extends Actor {
  import TVMessage._

  def receive = {
    case StartTVMsg =>
      println("Turn on TV please.")
      //val apollo = context.actorOf(Apollo.props)
      //apollo ! Apollo.Play
    case StopTVMsg =>
      println("Turn off TV please.")

  }
}


object CreateActors extends App{

  // Create the 'CreateActors' actor system
  val tvActorSystem = ActorSystem("actor-creation")

  // Create the 'tvActor' actor
  val tvActor = tvActorSystem.actorOf(Props[TVActor], "tv")

  //send StartTVMsg Message to actor
  tvActor ! TVMessage.StartTVMsg

  // Send StopTVMsg Message to actor
  tvActor ! TVMessage.StopTVMsg

  //shutdown system
  tvActorSystem.shutdown()

}
