package io.practice.data

import io.practice.models.{Page, Report}
import io.practice.services.ConfigService
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._

import scala.concurrent.Future
import scala.jdk.CollectionConverters.SeqHasAsJava

object Mongo {
  private val codecRegistry: CodecRegistry = fromRegistries(
    fromProviders(
      classOf[Page],
      classOf[Report]
    ),
    MongoClient.DEFAULT_CODEC_REGISTRY
  )

  private val db: MongoDatabase = getDb

  private def getDb: MongoDatabase = {
    val serverAddresses = ConfigService.getMongoConnectionString map { tup =>
      val (host, port) = tup
      new ServerAddress(host, port)
    }
    val mongoClient = if (ConfigService.getMongoAuthRequired) {
      val credential: MongoCredential = MongoCredential.createCredential(
        ConfigService.getMongoUser, ConfigService.getMongoDb, ConfigService.getMongoPassword.toCharArray
      )
      MongoClient(
        MongoClientSettings.builder()
          .applyToClusterSettings(b => b.hosts(serverAddresses.asJava))
          .credential(credential)
          .build()
      )
    } else {
      MongoClient(
        MongoClientSettings.builder()
          .applyToClusterSettings(b => b.hosts(serverAddresses.asJava))
          .build()
      )
    }
    mongoClient.getDatabase(ConfigService.getMongoDb).withCodecRegistry(codecRegistry)
  }

  private val pagesCollection = db.getCollection[Page]("pages")
  private val reportsCollection = db.getCollection[Report]("reports")

  def addReport(stopWords: List[String], projectId: String, pageId: String): Future[result.InsertOneResult] = {
    var report: Report = Report(stopWords, projectId, pageId)
    reportsCollection.insertOne(report).head()
  }

  def findReport(pageId: String): Future[Seq[Report]] = reportsCollection.find(equal("pageId", pageId)).toFuture()

  def getAllPages: Future[Seq[Page]] = pagesCollection.find().toFuture()
}
