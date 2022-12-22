package _05other_topics.jsonsupport_circe

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.circe._
import io.circe.generic.auto._

object JsonSupportCirce extends App {

  import Model._

  line80.green pipe println

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  val requestPayload = RequestPayload("some data")

  val response: Identity[Response[Either[ResponseException[String, io.circe.Error], ResponsePayload]]] =
    basicRequest
      .post(uri"https://httpbin.org/post")
      .body(requestPayload)
      .response(asJson[ResponsePayload])
      .send(backend)

  println(s"Response:\n$response")
  line10.cyan pipe println

  response.body match {
    case Left(ex)       =>
      println(s"$ex")
    case Right(payload) =>
      println(s"Response payload:\n$payload")
  }

  line80.green pipe println
}
