package edu.knoldus.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import edu.knoldus.models.{Checking, Customer}

import scala.concurrent.Await
import scala.concurrent.duration._

class ValidationActor(purchaseRef: ActorRef) extends Actor with ActorLogging {

  implicit val timeout = Timeout(10000 seconds)
  var senderRef: Option[ActorRef] = None

  override def receive = {
    case customer: Customer =>

      log.info("ValidationActor:>>>>checking handset Availability....")
      val availability = (purchaseRef ? Checking).mapTo[Boolean]

      if (Await.result(availability, 10.seconds)) {
        log.info("ValidationActor:>>>>Item available...Now processing for booking")
        purchaseRef ! customer
      }
      else {
        log.info("ValidationActor:>>>>Booking Failed")
      }
  }

}

object ValidationActor {

  def props(ref: ActorRef): Props = {
    Props(classOf[ValidationActor], ref)
  }

}