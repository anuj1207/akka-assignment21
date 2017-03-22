package edu.knoldus

import akka.actor.{ActorLogging, ActorSystem, Props}
import edu.knoldus.actors.{PurchaseActor, PurchaseRequestHandler, ValidationActor}
import edu.knoldus.models.Customer
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object BookingApplication extends App{

  val config = ConfigFactory.parseString(
    """
      |akka.actor.deployment {
      | /poolRouter {
      |   router = balancing-pool
      |   nr-of-instances = 5
      | }
      |}
    """.stripMargin
  )

  val system = ActorSystem("BookingGalaxy", config)
  implicit val timeout = Timeout(10000 seconds)

  val purchaseProps = Props[PurchaseActor]
  //  val system = ActorSystem("BookingGalaxy")

  val purchaseRef = system.actorOf(purchaseProps)
  val validateProps = Props(classOf[ValidationActor], purchaseRef)
  val validateRef = system.actorOf(validateProps)
  val purchaseHandlerProps = Props(classOf[PurchaseRequestHandler], validateRef)

  val purchaseHandlerRouter = system.actorOf(FromConfig.props(purchaseHandlerProps), "poolRouter")

  purchaseHandlerRouter ! Customer("Raman", "noida", 1234, 9871463)

  purchaseHandlerRouter ! Customer("Anuj", "noida", 1234, 9871463)

  purchaseHandlerRouter ! Customer("Gitika", "noida", 1234, 9871463)

  purchaseHandlerRouter ! Customer("Simar", "noida", 1234, 9871463)
}
