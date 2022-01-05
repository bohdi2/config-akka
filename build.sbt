name := "config-akka"

version := "1.0"

scalaVersion := "2.10.3"

fork in run := true

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.0",
  "org.scala-lang" % "scala-swing" % "2.10.3",
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test")
