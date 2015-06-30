import akka.actor.{Actor, ActorLogging, Props, ActorSystem}
import akka.persistence.{PersistentView, RecoveryCompleted, SnapshotOffer, PersistentActor}

object StatefulExample extends App {
  println("Let's get ready to persist!!")

  val actorSystem = ActorSystem("AkkaPersistence")

  val bankWatcher = actorSystem.actorOf(Props(classOf[BankWatcher], "chbatey"), "Watcher")
  val bank = actorSystem.actorOf(Props(classOf[BankAccount], "chbatey"), "Account")

  bank ! DepositCommand(100)
}


class BankAccount(customerId: String) extends PersistentActor with ActorLogging {

  private var state = new BankState(0)

  def receiveCommand: Receive = {
    case WithdrawCommand(amount) => persist(ChangeBalanceEvent(-amount))(updateState)
    case DepositCommand(amount) => persist(ChangeBalanceEvent(amount))(updateState)
    case _ => log.info("state {}", state)
  }

  def receiveRecover: Receive = {
    case event: ChangeBalanceEvent =>
      log.info("Recovering state from event {}", event)
      updateState(event)
    case SnapshotOffer(_, snapshot: BankState) =>
      log.info("Recovering from snapshot {}", snapshot)
      state = snapshot
    case RecoveryCompleted => log.info("Recovery complete")
  }

  def updateState(event: ChangeBalanceEvent): Unit = {
    state = state.updated(event)
    log.info("State be updated {}", state)
  }

  override def persistenceId: String = customerId
}

class BankWatcher(customerId: String) extends PersistentView with ActorLogging {
  override def viewId: String = s"$customerId-watcher"
  override def persistenceId: String = customerId

  override def receive: Actor.Receive =  {
    case msg @ _ => log.info("Msg {}", msg)
  }
}

case class WithdrawCommand(amount: Int)
case class DepositCommand(amount: Int)

case class ChangeBalanceEvent(amount: Int)

case class BankState(balance: Int) {
  def updated(event: ChangeBalanceEvent) : BankState = copy(balance = balance + event.amount)
}
