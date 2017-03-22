package edu.knoldus.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{CallingThreadDispatcher, EventFilter, ImplicitSender, TestActor, TestActorRef, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import edu.knoldus.models.{Checking, Customer}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

object ValidationActorSpec {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        |akka.loggers = [akka.testkit.TestEventListener]
      """.stripMargin
    )
    ActorSystem("test-system", config)
  }
}
import ValidationActorSpec._

class ValidationActorSpec extends TestKit(testSystem) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers with ImplicitSender{

  override protected def afterAll(): Unit = {
    system.terminate
  }

  "A ValidationActor" must {

    "successfully send req to purchase ref if item available" in {

      val newCustomer = Customer("Anuj", "Noida", 3325, 9871)
      val props = ValidationActor.props(testActor)
      val ref = system.actorOf(props)
      ref ! newCustomer
      expectMsg(Checking)
      //expectMsg(newCustomer)

    }

  }

  "A ValidationActor" must {

    val newCustomer = Customer("Anuj", "Noida", 3325, 9871)
    val probe = TestProbe()

    "test ask await condition with true return" in {
      val ref = TestActorRef(new ValidationActor(probe.ref))
      probe.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot =
          msg match {
            case Checking =>
              sender.tell(true, self)
              TestActor.KeepRunning

            case c: Customer =>
              testActor.tell(c, sender)
             TestActor.KeepRunning
          }
      })

      ref ! newCustomer
      expectMsg(newCustomer)
    }

    "test ask await with false return " in {

      probe.setAutoPilot(new TestActor.AutoPilot {
        override def run(sender: ActorRef, msg: Any) =
          msg match {
            case Checking =>
              sender.tell(false, self)
              TestActor.KeepRunning
          }
      })

      val dispatcherId = CallingThreadDispatcher.Id
      val props = ValidationActor.props(probe.ref).withDispatcher(dispatcherId)
      val ref = system.actorOf(props)

      EventFilter.info(message = "ValidationActor:>>>>Booking Failed", occurrences = 1)
        .intercept {
          ref ! newCustomer
        }

    }

  }

}
