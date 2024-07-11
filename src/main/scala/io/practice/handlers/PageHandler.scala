package io.practice.handlers

import io.practice.data.Mongo
import io.practice.executionContext
import io.practice.models.Page

import scala.collection.mutable.ListBuffer

import com.typesafe.scalalogging.Logger

class PageHandler(var whiteList: Set[String], var blackList: Set[String]) {

  private val logger = Logger(getClass.getName)

  def pageHandler(page: Page): Unit = {
    logger.debug(s"Handling page ${page._id}")
    if (isInWhiteList(page.projectId)) {
      return
    }
    val stopWords: List[String] = findStopWords(page.content)
    if (stopWords.nonEmpty) {
      Mongo.updateReport(stopWords, page.projectId, page._id.toString).map {updateResult =>
        logger.debug(s"Update result: ${updateResult.toString}")
      }
    }
  }

  private def findStopWords(content: String): List[String] = {
    var list: ListBuffer[String] = new ListBuffer[String]()
    for (stopWord <- blackList) {
      if (content.toLowerCase.contains(stopWord.toLowerCase)) {
        list.append(stopWord)
      }
    }
    list.toList
  }


  private def isInWhiteList(projectId: String): Boolean = {
    whiteList(projectId)
  }

}
