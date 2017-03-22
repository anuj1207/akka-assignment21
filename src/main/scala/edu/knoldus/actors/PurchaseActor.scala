package edu.knoldus.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import edu.knoldus.actors.PurchaseActor.GetState
import edu.knoldus.models.{Checking, Customer}

class PurchaseActor extends Actor with ActorLogging {

  var handsetCounter = 3

  override def receive = {
    case Checking if handsetCounter > 0 =>
      log.info(s"Purchase Actor:>>>> Item in stock  $handsetCounter")
      sender ! true

    case customer: Customer =>
      log.info("Purchase Actor:>>>> Processing booking")
      handsetCounter -= 1
      log.info(s"Purchase Actor:>>>> booked for ${customer.toString}")

    case GetState(ref) => ref ! handsetCounter

    case _ =>
      log.info("Purchase Actor:>>>> Out of stock")
      sender ! false
  }

}

object PurchaseActor {

  case class GetState(ref: ActorRef)

}
