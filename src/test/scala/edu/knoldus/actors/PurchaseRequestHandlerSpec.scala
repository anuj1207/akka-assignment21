package edu.knoldus.actors
import akka.actor.ActorSystem
import akka.actor.{ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import edu.knoldus.actors.PurchaseActor.GetState
import edu.knoldus.models.Customer
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

object PurchaseRequestHandlerSpec {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        |akka.loggers = [akka.testkit.TestEventListener]
      """.stripMargin
    )
    ActorSystem("test-system", config)
  }
}
import PurchaseRequestHandlerSpec._


class PurchaseRequestHandlerSpec  extends TestKit(testSystem) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers{

  override protected def afterAll(): Unit = {
    system.terminate
  }

  "A purchaseRequestHandler " must {

    "send request to validateActor if valid customer" in{

      val newCustomer = Customer("Anuj", "Noida", 3325, 9871)
      val props = PurchaseRequestHandler.props(testActor)
      val ref = system.actorOf(props, "PurchaseRequestHandler")
      ref ! newCustomer

      expectMsgPF() {
        case customer: Customer =>
          customer mustBe newCustomer
      }

    }

    "log about invalid customer " in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = PurchaseRequestHandler.props(testActor).withDispatcher(dispatcherId)
      val ref = system.actorOf(props)

      EventFilter.info(message = "PurchaseRequestHandler:>>>You are Not a customer", occurrences = 1)
        .intercept {
          ref ! "PurchaseRequestHandler:>>>You are Not a customer"
        }
    }

  }

}
