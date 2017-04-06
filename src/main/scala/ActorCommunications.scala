/**
  * Created by tki214 on 4/5/17.
  */
import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by tki214 on 4/5/17.
  */


object ChildActorMessage {
  case object sendMsgToParent

}

class ChildActor extends Actor {
  import ParentActorMessage._

  def receive = {
    case ParentActorMessage.sendMsgToChild =>
      println("Child send an message.")
      sender()! ChildActorMessage.sendMsgToParent

  }
}


object ParentActorMessage {
  case object sendMsgToChild
  case object MsgFromApp

}

class ParentActor extends Actor {
  import ParentActorMessage._

  def receive = {
    case MsgFromApp =>
      println("Message From APP.")
      val childActor = context.actorOf(Props[ChildActor])
      childActor ! ParentActorMessage.sendMsgToChild
    case ChildActorMessage.sendMsgToParent =>
      println("Message Received in Parent!!!!!")

  }
}


object ActorCommunications extends App{

  // Create the 'childParentActorSystem' actor system
  val childParentActorSystem = ActorSystem("actor-communications")

  // Create the 'parentActor' actor
  val parentActor = childParentActorSystem.actorOf(Props[ParentActor], "parentActor")

  //send StartTVMsg Message to actor
  parentActor ! ParentActorMessage.MsgFromApp

  //shutdown system
  childParentActorSystem.shutdown()

}
