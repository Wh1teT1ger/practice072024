package io.practice.handlers

import io.practice.data.Mongo
import io.practice.executionContext
import io.practice.models.Page

import scala.collection.mutable.ListBuffer

import com.typesafe.scalalogging.Logger

class PageHandler(var whiteList: Set[String], var blackList: Set[String]) {

  private val logger = Logger(getClass.getName)

  def pageHandler(page: Page): Unit = {
    if (isInWhiteList(page.projectId)) {
      return
    }
    val stopWords: List[String] = findStopWords(page.content)
    if (stopWords.nonEmpty) {
      Mongo.findReport(page._id.toString).map { seq =>
        if (seq.toList.isEmpty) {
          logger.info(s"Found new page ${page.projectId} with stop words: $stopWords")
          Mongo.addReport(stopWords, page.projectId, page._id.toString)
        }
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
