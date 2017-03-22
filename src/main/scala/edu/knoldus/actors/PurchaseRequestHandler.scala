package edu.knoldus.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import edu.knoldus.models.Customer

class PurchaseRequestHandler(validateRef: ActorRef) extends Actor with ActorLogging{

  override def receive = {
    case customer: Customer =>
      log.info("PurchaseRequestHandler:>>>>Request received for GALAXY S8 ....processing ahead")
      validateRef ! customer
    case _ =>
      log.info("PurchaseRequestHandler:>>>You are Not a customer")
  }

}

object PurchaseRequestHandler {

  def props(ref: ActorRef): Props = {
    Props(classOf[PurchaseRequestHandler], ref)
  }

}
