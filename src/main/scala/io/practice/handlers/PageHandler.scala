package io.practice.handlers

import com.typesafe.scalalogging.Logger
import io.practice.data.Mongo
import io.practice.executionContext
import io.practice.models.Page

import scala.collection.mutable.{ListBuffer, Queue}
import scala.language.postfixOps

class PageHandler(var whiteList: Set[String], var blackList: Set[String]) {

  private val logger = Logger(getClass.getName)

  private val engToRuMap: Map[Char, List[Char]] = Map('3' -> List('е'), '@' -> List('а'), 'a' -> List('а'), 'b' -> List('в', 'б'),
    'c' -> List('с'), 'd' -> List('д'), 'e' -> List('е', 'э'), 'f' -> List('ф'), 'g' -> List(), 'i' -> List('и'),
    'j' -> List('и'), 'h' -> List('н', 'х'), 'k' -> List('к'), 'l' -> List('л'), 'm' -> List('м', 'т'),
    'n' -> List('н', 'п', 'и'), 'o' -> List('о'), 'p' -> List('р', 'п'), 'r' -> List('r'), 's' -> List('с'),
    't' -> List('т'), 'u' -> List('u'), 'v' -> List('в'), 'x' -> List('х'), 'y' -> List('у'), 'z' -> List('з'))

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
        val fixedWords = fixWords(word)
        for (fixedWord <- fixedWords) {
          if (blackList(fixedWord)) {
            list.append(fixedWord)
          }
        }
      }
    }
    list.toList
  }

  private def isInWhiteList(projectId: String): Boolean = {
    whiteList(projectId)
  }

  private def fixWords(word: String): List[String] = {
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
    if (countEng > 0 && countRu > 0) convertStringToRu(word) else List(word)
  }

  private def convertStringToRu(word: String): List[String] = {
    val listWord: ListBuffer[String] = ListBuffer()
    val queue: Queue[StringBuilder] = new Queue()
    queue.enqueue(new StringBuilder(""))
    while (queue.nonEmpty) {
      val string = queue.dequeue()
      val lengthString = string.length()
      if (lengthString == word.length) {
        listWord.append(string.toString())
      } else {
        val c: Char = word.charAt(lengthString)
        if (!isCyrillic(c) || !engToRuMap.contains(c)) {
          string += c
          queue.enqueue(string)
        } else {
          for (char <- engToRuMap(c)) {
            val newString = string.clone().append(char)
            queue.enqueue(newString)
          }
        }
      }
    }
    listWord.toList
  }

  private def isLatin(c: Char): Boolean = c >= 'a' && c <= 'z'

  private def isCyrillic(c: Char): Boolean = c >= 'а' && c <= 'я'

}
