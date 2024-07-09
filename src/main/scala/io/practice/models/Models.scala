package io.practice.models

import org.bson.codecs.pojo.annotations.BsonProperty

case class Page(@BsonProperty("_id") id: String, content: String, projectId: String)

case class Report(@BsonProperty("_id") id: String, stopWords: List[String], projectId: String, pageId: String, addedAt: Long = System.currentTimeMillis())
