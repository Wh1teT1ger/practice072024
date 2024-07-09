package io.practice

import io.practice.services.AntifraudService

object App {
  def main(args: Array[String]): Unit = {
    AntifraudService.start()
  }
}
