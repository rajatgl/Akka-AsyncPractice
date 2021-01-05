import java.net.http.{HttpClient, HttpRequest}

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCode}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.parsing.json.{JSON, JSONObject}
import scala.util.{Failure, Success}
import spray.json._

/**
 * Created on 1/2/2021.
 * Class: APIMain.scala
 * Author: Rajat G.L.
 */
object Routes extends App with Directives {
  //host and port numbers set via respective environment variables
  val host = System.getenv("Host")
  val port = System.getenv("Port").toInt
  private val apiKey = "67adaed5605f4aa0ba899fb73c0d9ef2"

  //maintains a pool of actors
  implicit val system: ActorSystem = ActorSystem("AS")
  //maintains and executes actor system
  implicit val executor: ExecutionContext = system.dispatcher

  // Handling Arithmetic and Null Pointer Exceptions
  val exceptionHandler = ExceptionHandler {
    case _: ArithmeticException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(400, entity = "Number could not be parsed. Is there a text were a number should be?"))
      }
    case _: NullPointerException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(402, entity = "Null value found while parsing the data. Contact the admin."))
      }
    case _: Exception =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(408, entity = "Some error occured. Please try again later."))
      }
  }

  /**
   * handles all the get post requests to appropriate path endings
   * @return
   */
  def route : Route =
    handleExceptions(exceptionHandler) {
      Directives.concat(
        Directives.get {
          Directives.concat(
            // GET "/getJson" path to fetch user objects in JSON format
            path("request") {
              val url = "http://newsapi.org/v2/everything?q=bitcoin&sortBy=publishedAt&apiKey="+apiKey
              val request = HttpRequest.newBuilder().GET().uri(java.net.URI.create(url)).build()
              val client = HttpClient.newBuilder().build()
              val response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString())

              val statusCode: StatusCode = response.statusCode()
              if(!statusCode.equals(StatusCodes.OK)){
                println(response.statusCode() + ": " + response.body())
                complete("URL did not respond, hence we could not save the data. Try again later!")
              }
              else{
                //db operations
                println(response.body())
                val jsonResult = response.body().parseJson
                //Config.sendCSVRequest(jsonResult)
                onComplete(Config.sendRequest(jsonResult)){
                  _ => complete("Downloaded your request and it has been stored with us. Thank you!")
                }
              }
            })
        })
    }

  val binder = Http().newServerAt(host,port).bind(route)
  binder.onComplete {
    case Success(serverBinding) => println(println(s"Listening to ${serverBinding.localAddress}"))
    case Failure(error) => println(s"Error : ${error.getMessage}")
  }
}
