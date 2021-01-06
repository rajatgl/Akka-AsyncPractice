name := "AsyncPractice"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(  "com.typesafe.akka" %% "akka-actor" % "2.5.32",  "com.typesafe.akka" %% "akka-stream" % "2.5.32",  "com.typesafe.akka" %% "akka-http" % "10.2.2")
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % "2.0.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.2"
libraryDependencies += "au.com.bytecode" % "opencsv" % "2.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"