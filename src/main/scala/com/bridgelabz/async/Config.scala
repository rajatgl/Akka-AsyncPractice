package com.bridgelabz.async

import java.io.{BufferedWriter, FileWriter}
import java.util
import java.util.Date

import au.com.bytecode.opencsv.CSVWriter
import com.bridgelabz.async.Routes.executor
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, SingleObservable}
import spray.json.JsValue

import scala.concurrent.Future

/**
 * Created on 1/5/2021.
 * Class: Config.scala
 * Author: Rajat G.L.
 */
object Config {

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val collection: MongoCollection[Document] = database.getCollection("getRequests")

  /**
   *
   * @param news : Data to be added into database
   * @return : Future[Done]
   */
  def sendRequest(news: JsValue): Future[SingleObservable[Completed]] = {

    Future {
      var obj = news.asJsObject

      val doc: Document = Document("status" -> obj.fields("status").toString(),
        "totalResults" -> obj.fields("totalResults").toString()
      )
      collection.insertOne(doc)
    }
  }

  def sendCSVRequest(news: JsValue): Future[String] = {

    Future {
      try {
        val outputFile = new BufferedWriter(new FileWriter("assets/output.csv", true))
        val csvWriter = new CSVWriter(outputFile)
        val status = news.asJsObject.fields("status").toString()
        val totalResults = news.asJsObject.fields("totalResults").toString()
        var requestTime = new Date().getTime.toString
        requestTime = requestTime.substring(requestTime.length - 5, requestTime.length)

        val listOfRecords = new util.ArrayList[Array[String]]()
        listOfRecords.add(Array(status, totalResults, requestTime))

        csvWriter.writeAll(listOfRecords)
        outputFile.close()
        "Successfully added to CSV"
      }
      catch {
        case throwable: Throwable => throw throwable
      }
    }
  }
}
