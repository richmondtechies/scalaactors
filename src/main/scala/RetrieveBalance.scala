import akka.persistence._
import akka.actor.{ Actor, ActorRef, ActorSystem, Props, ActorLogging }

object RetrieveBalance{
  sealed trait Operation {
    val balance: Int
  }

  case class Increment(override val balance: Int) extends Operation
  case class Decrement(override val balance: Int) extends Operation

  case class Cmd(op: Operation)
  case class Evt(op: Operation)

  case class State(balance: Int)

}


class RetrieveBalance extends PersistentActor with ActorLogging {
  import RetrieveBalance._

  println("Starting ........................")

  // Persistent Identifier
  override def persistenceId = "RetrieveBalance"

  var state: State = State(balance= 0)

  def updateState(evt: Evt): Unit = evt match {
    case Evt(Increment(balance)) =>
      state = State(balance = state.balance + balance)
      takeSnapshot
    case Evt(Decrement(balance)) =>
      state = State(balance = state.balance - balance)
      takeSnapshot
  }

  // Persistent receive on recovery mood
  val receiveRecover: Receive = {
    case evt: Evt =>
      println(s"RetrieveBalance receive ${evt} on recovering mood")
      updateState(evt)
    case SnapshotOffer(_, snapshot: State) =>
      println(s"RetrieveBalance receive snapshot with data: ${snapshot} on recovering mood")
      state = snapshot
    case RecoveryCompleted =>
      println(s"Recovery Complete and Now I'll swtich to receiving mode :)")

  }

  // Persistent receive on normal mood
  val receiveCommand: Receive = {
    case cmd @ Cmd(op) =>
      println(s"RetrieveBalance receive ${cmd}")
      persist(Evt(op)) { evt =>
        updateState(evt)
      }

    case "print" =>
      println(s"The Current Balance is ${state}")

    case SaveSnapshotSuccess(metadata) =>
      println(s"save snapshot succeed.")
    case SaveSnapshotFailure(metadata, reason) =>
      println(s"save snapshot failed and failure is ${reason}")

  }

  def takeSnapshot = {
    if(state.balance % 5 == 0){
      saveSnapshot(state)
    }
  }

//  override def recovery = Recovery.none

}








