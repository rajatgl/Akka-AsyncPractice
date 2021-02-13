package com.bridgelabz.async

import java.io.{BufferedWriter, FileWriter}
import java.util
import java.util.Date

import au.com.bytecode.opencsv.CSVWriter
import com.bridgelabz.async.Routes.executor
import com.typesafe.scalalogging.Logger
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, SingleObservable}
import spray.json.{JsArray, JsValue}

import scala.concurrent.Future

/**
 * Created on 1/5/2021.
 * Class: Config.scala
 * Author: Rajat G.L.
 */
class Config {

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val collection: MongoCollection[Document] = database.getCollection("getRequests")
  private val logger = Logger("Config")

  /**
   *
   * @param news : Data to be added into database
   * @return : Future[Done]
   */
  def sendRequest(news: JsValue): Future[Completed] = {
    val obj = news.asJsObject
    val doc: Document = Document("status" -> obj.fields("status").toString(),
      "totalResults" -> obj.fields("totalResults").toString()
    )
    collection.insertOne(doc).toFuture()
  }

  /**
   *
   * @param news Data to be written into csv file
   * @return confirmation message
   */
  def sendCSVRequest(news: JsValue, outputFilePath: String = System.getenv("OUTPUT_FILE")): Future[String] = {

    try {
      val outputFile = new BufferedWriter(new FileWriter(outputFilePath, true))
      val csvWriter = new CSVWriter(outputFile)
      val status = news.asJsObject.fields("status").toString()
      val totalResults = news.asJsObject.fields("totalResults").toString()
      val articles = news.asJsObject.fields("articles").asInstanceOf[JsArray]
      var requestTime = new Date().getTime.toString
      requestTime = requestTime.substring(requestTime.length - 5, requestTime.length)

      val listOfRecords = new util.ArrayList[Array[String]]()
      //listOfRecords represents multiple rows

      val listOfStrings = new util.ArrayList[String]()
      //listOfStrings represents a row in the csv (a comma separated vector)

      listOfRecords.add(Array("Total Number Of Articles: " + totalResults))
      for (element <- articles.elements) {
        listOfRecords.add(Array("Title: " + element.asJsObject.fields("title").toString()))
      }

      //write data
      csvWriter.writeAll(listOfRecords)
      outputFile.close()

      logger.debug("File successfully handled")
      Future.successful("Successfully added to CSV")
    }
    catch {
      case exception: Exception =>
        logger.error(exception.getMessage)
        Future.failed[String](new Throwable("News could not be accessed. Try again later."))
    }
  }
}
