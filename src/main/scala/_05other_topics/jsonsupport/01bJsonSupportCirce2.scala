package _05other_topics.jsonsupport

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.circe._
import io.circe.generic.auto._

object JsonSupportCirce2 extends App {

  import Model._

  final case class Origin(origin: String)

  line80.green pipe println

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  val requestPayload = RequestPayload("some data")

  val response: Identity[Response[Either[ResponseException[String, io.circe.Error], Origin]]] =
    basicRequest
      .post(uri"https://httpbin.org/post")
      .body(requestPayload)
      .response(asJson[Origin])
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
