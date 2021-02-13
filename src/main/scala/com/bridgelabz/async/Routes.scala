package com.bridgelabz.async

import java.net.http.{HttpClient, HttpRequest}

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCode}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import spray.json._

/**
 * Created on 1/2/2021.
 * Class: APIMain.scala
 * Author: Rajat G.L.
 */
object Routes extends App with Directives {

  // $COVERAGE-OFF$
  //host and port numbers set via respective environment variables
  val host = System.getenv("Host")
  val port = System.getenv("Port").toInt
  val apiKey = System.getenv("NEWS_API_KEY")

  val logger = Logger("Routes")
  //maintains a pool of actors
  implicit val system: ActorSystem = ActorSystem("AS")
  //maintains and executes actor system
  implicit val executor: ExecutionContext = system.dispatcher

  // Handling Arithmetic and Null Pointer Exceptions
  val exceptionHandler = ExceptionHandler {
    case aex: ArithmeticException =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally: ${aex.getMessage}")
        complete(HttpResponse(StatusCodes.INTERNAL_SERVER_ERROR.intValue(), entity = "Number could not be parsed. Is there a text were a number should be?"))
      }
    case nex: NullPointerException =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally: ${nex.getMessage}")
        complete(HttpResponse(StatusCodes.INTERNAL_SERVER_ERROR.intValue(), entity = "Null value found while parsing the data. Contact the admin."))
      }
    case ex: Exception =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally: ${ex.getMessage}")
        complete(HttpResponse(StatusCodes.INTERNAL_SERVER_ERROR.intValue(), entity = "Some error occured. Please try again later."))
      }
  }


  // $COVERAGE-ON$
  /**
   * handles all the get post requests to appropriate path endings
   *
   * @return Future[Done]
   */
  def route(apiKey: String = apiKey, config: Config = new Config): Route =
    handleExceptions(exceptionHandler) {
      Directives.concat(
        Directives.get {
          Directives.concat(
            //Get path to receive confirmation message regarding data insertion
            path("request") {

              val url = "http://newsapi.org/v2/everything?q=bitcoin&sortBy=publishedAt&apiKey=" + apiKey
              val request = HttpRequest.newBuilder().GET().uri(java.net.URI.create(url)).build()
              val client = HttpClient.newBuilder().build()
              val response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString())

              val statusCode: StatusCode = response.statusCode()
              if (!statusCode.equals(StatusCodes.OK)) {
                logger.debug(url + ": " + response.statusCode() + ": " + response.body())
                complete("URL did not respond, hence we could not save the data. Try again later!")
              }
              else {
                //converts JavaString to JSON
                val jsonResult = response.body().parseJson

                val csvFuture = config.sendCSVRequest(jsonResult)
                val dbFuture = config.sendRequest(jsonResult)

                var printMessage: String = "Successfully downloaded and inserted the data. Thank you!"
                csvFuture.onComplete {
                  case Failure(_) => printMessage = "CSV file could not be updated."
                }
                dbFuture.onComplete {
                  case Failure(_) => printMessage = "Database could not be updated."
                }

                complete(printMessage)
              }
            })
        })
    }

  // $COVERAGE-OFF$
  //Server Binding
  val binder = Http().newServerAt(host, port).bind(route())
  binder.onComplete {
    case Success(serverBinding) => logger.info(s"Listening to ${serverBinding.localAddress}")
    case Failure(error) => logger.error(s"Error : ${error.getMessage}")
  }
}
