package io

import akka.actor.{ActorSystem, Scheduler}
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor

package object practice {
  val config: Config = ConfigFactory.load().getConfig("akka")
  implicit val system: ActorSystem = ActorSystem("practice", config)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val scheduler: Scheduler = system.scheduler
}
