/**
  * Created by tki214 on 4/5/17.
  */
import MonitorChildException.{RestartChildException}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props}

/**
  * Created by tki214 on 4/5/17.
  */



class MonitorChildActor extends Actor {

  override def preStart() = {
    println("MonitorChildActor preStart hook....")
    super.preStart()
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    println("MonitorChildActor preRestart hook...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    println("MonitorChildActor postRestart hook...")
    super.postRestart(reason)
  }

  override def postStop() = {
    println("MonitorChildActor postStop...")
    super.postStop()
  }

  def receive = {
    case throwException =>
      throw RestartChildException

  }
}


object MonitorChildException {
  case object ResumeChildException extends Exception
  case object StopChildException extends Exception
  case object RestartChildException extends Exception
}


object ParentActorExceptionMessage {
  case object throwException

}

class ParentSupervisorActor extends Actor {
  import MonitorChildException._


  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10) {
      case ResumeChildException      => Resume
      case RestartChildException     => Restart
      case StopChildException        => Stop
      case _: Exception              => Escalate
    }


  def receive = {
    case ParentActorExceptionMessage.throwException =>
      println("Send Message to child to throw an exception..")
      val childActor = context.actorOf(Props[MonitorChildActor])
      childActor ! ParentActorExceptionMessage.throwException

  }
}


object ActorSuperVision extends App{

  // Create the 'childParentActorSystem' actor system
  val childParentActorSystem = ActorSystem("actor-supervision")

  // Create the 'parentActor' actor
  val parentActor = childParentActorSystem.actorOf(Props[ParentSupervisorActor], "parentActor")

  //send StartTVMsg Message to actor
  parentActor ! ParentActorExceptionMessage.throwException


  Thread.sleep(2000)
  println()

  //shutdown system
  childParentActorSystem.shutdown()

}
