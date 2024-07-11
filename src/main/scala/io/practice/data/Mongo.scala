package io.practice.data

import com.typesafe.scalalogging.Logger
import io.practice.models.{Page, Report}
import io.practice.services.ConfigService
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.UpdateResult

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

  private val logger = Logger(getClass.getName)

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

  def updateReport(stopWords: List[String], projectId: String, pageId: String): Future[UpdateResult] =
    reportsCollection.updateOne(equal("pageId", pageId), combine(set("stopWords", stopWords), set("projectId", projectId),
      set("pageId", pageId), set("addedAt", System.currentTimeMillis())), UpdateOptions().upsert(true)).head()

  def getAllPages(handler: Page => Unit, batchSize: Int = Int.MaxValue): Unit =
    pagesCollection.find().batchSize(batchSize).subscribe((page: Page) => handler(page),
      (e: Throwable) => logger.error(s"There was an error: $e"),
      () => logger.debug("Completed"))
}
