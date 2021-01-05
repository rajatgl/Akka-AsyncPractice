/**
 * Created on 1/2/2021.
 * Class: Config.scala
 * Author: Rajat G.L.
 */

import java.io.{BufferedWriter, FileWriter}

import au.com.bytecode.opencsv.CSVWriter

import scala.concurrent.Future
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, MongoExecutionTimeoutException}
import spray.json.JsValue

import scala.collection.mutable.ListBuffer
import scala.util.Random

import java.util

object Config {

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val collection: MongoCollection[Document] = database.getCollection("getRequests")

  /**
   *
   * @param news : Data to be added into database
   * @return : Future[Done]
   */
  def sendRequest(news: JsValue): Future[Completed] = {

    var obj = news.asJsObject

    val doc: Document = Document("status" -> obj.fields("status").toString(),
      "totalResults" -> obj.fields("totalResults").toString()
    )
    collection.insertOne(doc).toFuture()
  }

  def sendCSVRequest(news: JsValue): Unit = {
    val outputFile = new BufferedWriter(new FileWriter("assets/output.csv"))
    val csvWriter = new CSVWriter(outputFile)
    val csvSchema = Array("status", "totalResults")
    val status = news.asJsObject.fields("status").toString()
    val totalResults = news.asJsObject.fields("totalResults").toString()
    val random = new Random()

    var listOfRecords = new util.ArrayList[Array[String]]()
    listOfRecords.add(csvSchema)
    listOfRecords.add(Array(status, totalResults))

    csvWriter.writeAll(listOfRecords)
    outputFile.close()
  }
}
