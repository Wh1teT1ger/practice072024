package io.practice.services

import java.io.FileInputStream
import java.util.Properties

object ConfigService {
  private val p = new Properties
  private val stream = new FileInputStream("conf.d/server.properties")
  p.load(stream)
  stream.close()
  System.setProperty("config.file", "conf.d/application.conf")

  def getMongoConnectionString: List[(String, Int)] = getStringParam("mongo_host", "mongodb://localhost:27017")
    .split(";").toList
    .map(x => (x, getMongoPort))

  def getMongoPort: Int = getIntParam("mongo_port", 27017)

  def getMongoAuthRequired: Boolean = getBoolParam("mongo_auth_required")

  def getMongoDb: String = getStringParam("mongo_db")

  def getMongoUser: String = getStringParam("mongo_user")

  def getMongoPassword: String = getStringParam("mongo_password")

  private def getStringParam(paramName: String, defaultValue: String = ""): String = {
    val paramValue = p.getProperty(paramName)
    if (paramValue == null) defaultValue else paramValue
  }

  private def getIntParam(paramName: String, defaultValue: Int = 0): Int = {
    val paramValue = p.getProperty(paramName)
    if (paramValue == null) defaultValue else paramValue.toInt
  }

  private def getLongParam(paramName: String, defaultValue: Long = 0L): Long = {
    val paramValue = p.getProperty(paramName)
    if (paramValue == null) defaultValue else paramValue.toLong
  }

  private def getBoolParam(paramName: String, defaultValue: Boolean = false): Boolean = {
    val paramValue = p.getProperty(paramName)
    if (paramValue == null) defaultValue else paramValue.toBoolean
  }
}
