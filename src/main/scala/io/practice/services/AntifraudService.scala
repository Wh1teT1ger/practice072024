package io.practice.services

import akka.actor.Scheduler
import io.practice
import io.practice.data.Mongo
import io.practice.executionContext

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object AntifraudService {
  private val pollInterval = 5 seconds
  private val scheduler: Scheduler = practice.scheduler

  private val stopList = Set("хакер", "взлом", "ddos", "доход", "PayPal", "фрибет")

  private val whiteList = Set("notSoBad")

  def start(): Unit = {
    scheduler.scheduleWithFixedDelay(pollInterval, pollInterval) { () =>
      scanPages()
    }
  }

  private def scanPages(): Unit = {
    Mongo.getAllPages.map { pagesSeq =>
      println("hello world")
    }
  }
}