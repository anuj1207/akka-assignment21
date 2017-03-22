package edu.knoldus.actors
import akka.actor.ActorSystem
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import edu.knoldus.actors.PurchaseActor.GetState
import edu.knoldus.models.{Checking, Customer}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class PurchaseActorSpec extends TestKit(ActorSystem("test-system")) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers with ImplicitSender {

  override protected def afterAll(): Unit = {
    system.terminate
  }

  "A purchaseActor " must{

    val purchaseActor = system.actorOf(Props[PurchaseActor])
    "change state when receive a customer" in{
      purchaseActor ! Customer("", "", 1, 9)
      purchaseActor ! GetState(testActor)
      expectMsg(2)
    }

    "return true when handset available" in{
//      val purchaseActor = system.actorOf(Props[PurchaseActor])
      purchaseActor ! Checking
      expectMsg(true)
    }

    "return false when invalid input" in{
//      val purchaseActor = system.actorOf(Props[PurchaseActor])
      purchaseActor ! "kuch bhi"
      expectMsg(false)
    }

    "return false when handset not available" in{
      val purchaseActor = system.actorOf(Props[PurchaseActor])
      purchaseActor ! Customer("", "", 1, 9)
      purchaseActor ! Customer("", "", 1, 9)
      purchaseActor ! Customer("", "", 1, 9)
      purchaseActor ! Checking
      expectMsg(false)
    }

  }

}
