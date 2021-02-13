name := "AsyncPractice"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(  "com.typesafe.akka" %% "akka-actor" % "2.5.32",  "com.typesafe.akka" %% "akka-stream" % "2.5.32",  "com.typesafe.akka" %% "akka-http" % "10.2.2")
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % "2.0.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.2"
libraryDependencies += "au.com.bytecode" % "opencsv" % "2.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.32"
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.2.2"
libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.3.0" % "test"
