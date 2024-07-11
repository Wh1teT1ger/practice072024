package io.practice.services

import akka.actor.Scheduler
import com.typesafe.scalalogging.Logger
import io.practice
import io.practice.data.Mongo
import io.practice.executionContext
import io.practice.handlers.PageHandler

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object AntifraudService {
  private val logger = Logger(getClass.getName)
  private val pollInterval = 5 seconds
  private val scheduler: Scheduler = practice.scheduler

  private val stopList = Set("хакер", "взлом", "ddos", "доход", "PayPal", "фрибет")

  private val whiteList = Set("notSoBad")

  private val pageHandler = new PageHandler(whiteList, stopList)

  def start(): Unit = {
    logger.info("Start service")
    scheduler.scheduleWithFixedDelay(pollInterval, pollInterval) { () =>
      logger.debug("Start scanning")
      scanPages()
    }
  }

  private def scanPages(): Unit = {
    Mongo.getAllPages(pageHandler.pageHandler, 20)
  }
}
