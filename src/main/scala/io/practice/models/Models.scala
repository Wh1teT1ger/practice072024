package io.practice.models

import org.bson.codecs.pojo.annotations.BsonProperty
import org.mongodb.scala.bson.ObjectId

case class Page( _id: ObjectId, content: String, projectId: String)

case class Report( _id: ObjectId, stopWords: List[String], projectId: String, pageId: String, addedAt: Long = System.currentTimeMillis())
