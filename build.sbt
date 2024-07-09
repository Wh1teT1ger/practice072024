import scala.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

val akkaVersion = "2.8.5"
val mongoDriverVersion = "4.5.1"
val redisScalaVersion = "1.9.0"

lazy val root = (project in file("."))
  .settings(
    name := "practice072024",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "org.mongodb.scala" %% "mongo-scala-driver" % mongoDriverVersion,
      "org.mongodb.scala" %% "mongo-scala-bson" % mongoDriverVersion,
      "com.github.etaty" %% "rediscala" % redisScalaVersion,
    )
  )
