name := "akka-assignment-21"

version := "1.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.17"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1"
)

coverageExcludedPackages:="edu\\.knoldus\\.BookingApplication.*"
    