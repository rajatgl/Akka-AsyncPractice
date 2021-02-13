package com.bridgelabz.asynctest

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.bridgelabz.async.{Config, Routes}
import org.mockito.Mockito.when
import org.mongodb.scala.Completed
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatestplus.mockito.MockitoSugar
import spray.json._

import scala.concurrent.{Await, Future}

// Testing does not require style checks
//scalastyle:off
class AsyncTest extends AsyncFlatSpec with ScalatestRouteTest with MockitoSugar{

  behavior of "sendCSVRequest"
  it should "eventually throw an error since passing a null object" in {

    val configMock = new Config

    val csvFuture = configMock.sendCSVRequest(null)
    ScalaFutures.whenReady(csvFuture.failed){
      e => e shouldBe a [Throwable]
    }
  }

  it should "add content to the CSV file if valid news object sent" in {

    val configMock = new Config

    val responseBody = "{\"status\":\"ok\",\"totalResults\":8217,\"articles\":[{\"source\":{\"id\":null,\"name\":\"manager-magazin.de\"},\"author\":\"Cyrus de la Rubia\",\"title\":\"Bitcoin: Kryptowährung auf Rekordhoch, Kursziel 1 Million US-Dollar durch Stromverbrauch unwahrscheinlich\",\"description\":\"Der sagenhafte Kursanstieg des Bitcoin macht selbst Experten ratlos. Kennt der Preis der Kryptowährung keine Grenze? Doch. Sie hat mit dem hohen Energieverbrauch zu tun - und lässt sich sogar ökonomisch bestimmen.\",\"url\":\"https://www.manager-magazin.de/finanzen/bitcoin-kryptowaehrung-auf-rekordhoch-kursziel-1-million-us-dollar-durch-stromverbrauch-unwahrscheinlich-a-06c16bc4-c818-4018-ba08-14ac452af835\",\"urlToImage\":\"https://cdn.prod.www.manager-magazin.de/images/19da2063-0001-0004-0000-000000669600_w1280_r1.77_fpx69.18_fpy54.99.jpg\",\"publishedAt\":\"2021-01-06T09:02:00Z\",\"content\":\"Der Preis für die bekannteste Kryptowährung der Welt steigt und steigt. Im Lauf des Jahres 2020 hat sich der Kurs des Bitcoins auf 29.000 US-Dollar vervierfacht, mittlerweile liegt er auf der Handels… [+6729 chars]\"}]}"
    val newsValue = responseBody.parseJson
    val csvFuture = configMock.sendCSVRequest(newsValue, "C:\\Users\\rajath\\IdeaProjects\\AsyncPractice\\assets\\outputTEST.csv")
    val csvOp = Await.result(csvFuture, 2.seconds)

    assert(csvOp.equals("Successfully added to CSV"))
  }
  it should "return confirmation message if successfully added" in {

    val configMock = new Config

    val responseBody = "{\"status\":\"ok\",\"totalResults\":8217,\"articles\":[{\"source\":{\"id\":null,\"name\":\"manager-magazin.de\"},\"author\":\"Cyrus de la Rubia\",\"title\":\"Bitcoin: Kryptowährung auf Rekordhoch, Kursziel 1 Million US-Dollar durch Stromverbrauch unwahrscheinlich\",\"description\":\"Der sagenhafte Kursanstieg des Bitcoin macht selbst Experten ratlos. Kennt der Preis der Kryptowährung keine Grenze? Doch. Sie hat mit dem hohen Energieverbrauch zu tun - und lässt sich sogar ökonomisch bestimmen.\",\"url\":\"https://www.manager-magazin.de/finanzen/bitcoin-kryptowaehrung-auf-rekordhoch-kursziel-1-million-us-dollar-durch-stromverbrauch-unwahrscheinlich-a-06c16bc4-c818-4018-ba08-14ac452af835\",\"urlToImage\":\"https://cdn.prod.www.manager-magazin.de/images/19da2063-0001-0004-0000-000000669600_w1280_r1.77_fpx69.18_fpy54.99.jpg\",\"publishedAt\":\"2021-01-06T09:02:00Z\",\"content\":\"Der Preis für die bekannteste Kryptowährung der Welt steigt und steigt. Im Lauf des Jahres 2020 hat sich der Kurs des Bitcoins auf 29.000 US-Dollar vervierfacht, mittlerweile liegt er auf der Handels… [+6729 chars]\"}]}"
    val newsValue = responseBody.parseJson
    val dbFuture = configMock.sendRequest(newsValue)
    val dbOp = Await.result(dbFuture, 2.seconds)
    assert(dbOp.isInstanceOf[Completed])
  }

  behavior of "Routes"
  it should "deny users to make request to invalid CSV file" in {

    val responseBody = "{\"status\":\"ok\",\"totalResults\":8217,\"articles\":[{\"source\":{\"id\":null,\"name\":\"manager-magazin.de\"},\"author\":\"Cyrus de la Rubia\",\"title\":\"Bitcoin: Kryptowährung auf Rekordhoch, Kursziel 1 Million US-Dollar durch Stromverbrauch unwahrscheinlich\",\"description\":\"Der sagenhafte Kursanstieg des Bitcoin macht selbst Experten ratlos. Kennt der Preis der Kryptowährung keine Grenze? Doch. Sie hat mit dem hohen Energieverbrauch zu tun - und lässt sich sogar ökonomisch bestimmen.\",\"url\":\"https://www.manager-magazin.de/finanzen/bitcoin-kryptowaehrung-auf-rekordhoch-kursziel-1-million-us-dollar-durch-stromverbrauch-unwahrscheinlich-a-06c16bc4-c818-4018-ba08-14ac452af835\",\"urlToImage\":\"https://cdn.prod.www.manager-magazin.de/images/19da2063-0001-0004-0000-000000669600_w1280_r1.77_fpx69.18_fpy54.99.jpg\",\"publishedAt\":\"2021-01-06T09:02:00Z\",\"content\":\"Der Preis für die bekannteste Kryptowährung der Welt steigt und steigt. Im Lauf des Jahres 2020 hat sich der Kurs des Bitcoins auf 29.000 US-Dollar vervierfacht, mittlerweile liegt er auf der Handels… [+6729 chars]\"}]}"
    val newsValue = responseBody.parseJson

    val configMock = mock[Config]
    when(configMock.sendCSVRequest(newsValue)).thenReturn(Future.failed(new Throwable))

    val apiKey = System.getenv("NEWS_API_KEY")
    Get("/request") ~> Routes.route(apiKey) ~> check {
      assert(!status.isSuccess())
    }
  }

  it should "deny users to make request to invalid database" in {

    val responseBody = "{\"status\":\"ok\",\"totalResults\":8217,\"articles\":[{\"source\":{\"id\":null,\"name\":\"manager-magazin.de\"},\"author\":\"Cyrus de la Rubia\",\"title\":\"Bitcoin: Kryptowährung auf Rekordhoch, Kursziel 1 Million US-Dollar durch Stromverbrauch unwahrscheinlich\",\"description\":\"Der sagenhafte Kursanstieg des Bitcoin macht selbst Experten ratlos. Kennt der Preis der Kryptowährung keine Grenze? Doch. Sie hat mit dem hohen Energieverbrauch zu tun - und lässt sich sogar ökonomisch bestimmen.\",\"url\":\"https://www.manager-magazin.de/finanzen/bitcoin-kryptowaehrung-auf-rekordhoch-kursziel-1-million-us-dollar-durch-stromverbrauch-unwahrscheinlich-a-06c16bc4-c818-4018-ba08-14ac452af835\",\"urlToImage\":\"https://cdn.prod.www.manager-magazin.de/images/19da2063-0001-0004-0000-000000669600_w1280_r1.77_fpx69.18_fpy54.99.jpg\",\"publishedAt\":\"2021-01-06T09:02:00Z\",\"content\":\"Der Preis für die bekannteste Kryptowährung der Welt steigt und steigt. Im Lauf des Jahres 2020 hat sich der Kurs des Bitcoins auf 29.000 US-Dollar vervierfacht, mittlerweile liegt er auf der Handels… [+6729 chars]\"}]}"
    val newsValue = responseBody.parseJson

    val configMock = mock[Config]
    when(configMock.sendCSVRequest(newsValue)).thenReturn(Future.successful("Success"))
    when(configMock.sendRequest(newsValue)).thenReturn(Future.failed(new Throwable))

    val apiKey = System.getenv("NEWS_API_KEY")
    Get("/request") ~> Routes.route(apiKey) ~> check {
      assert(!status.isSuccess())
    }
  }

  it should "deny users to make request with invalid API key" in {

    Get("/request") ~> Routes.route("invalid") ~> check {
      assert(!status.isSuccess())
    }
  }
}
