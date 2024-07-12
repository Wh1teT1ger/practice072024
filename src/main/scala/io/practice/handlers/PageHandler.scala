package io.practice.handlers

import com.typesafe.scalalogging.Logger
import io.practice.data.Mongo
import io.practice.executionContext
import io.practice.models.Page

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

class PageHandler(var whiteList: Set[String], var blackList: Set[String]) {

  private val logger = Logger(getClass.getName)

  private val engToRuMap: Map[Char, Char] = Map('a' -> 'а', 'b' -> 'в', 'c' -> 'с', 'd' -> 'д', 'e' -> 'е',
    'h' -> 'н', 'o' -> 'о', 't' -> 'т', 'y' -> 'у', 'p' -> 'р', 'k' -> 'к', 'x' -> 'х', 'm' -> 'м')

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
    val list: ListBuffer[String] = new ListBuffer[String]()
    val listContent = content.toLowerCase.split("[-–/&\\n.,()#|:;'\"_?!=<> ]")
    for (word <- listContent) {
      if (word.nonEmpty) {
        val fixedWord = fixWord(word)
        if (blackList(fixedWord)) {
          list.append(fixedWord)
        }
      }
    }
    list.toList
  }

  private def isInWhiteList(projectId: String): Boolean = {
    whiteList(projectId)
  }

  private def fixWord(word: String): String = {
    var countRu: Int = 0
    var countEng: Int = 0
    for (char <- word) {
      if (isLatin(char)) {
        countEng += 1
      }
      if (isCyrillic(char)) {
        countRu += 1
      }
    }
    if (countEng > 0 && countRu > 0) convertStringToRu(word) else word
  }

  private def convertStringToRu(word: String): String = {
    val newWord = new StringBuilder("")
    for (c <- word) {
      if (isLatin(c)) {
        newWord += (if (engToRuMap.contains(c)) engToRuMap(c) else c)
      } else {
        newWord += c
      }
    }
    newWord.toString()
  }

  private def isLatin(c: Char): Boolean = c >= 'a' && c <= 'z'

  private def isCyrillic(c: Char): Boolean = c >= 'а' && c <= 'я'

}
