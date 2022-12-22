package _05other_topics.jsonsupport

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.json4s._

object JsonSupportJson4s extends App {

  import Model._

  line80.green pipe println

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  val requestPayload = RequestPayload("some data")

  implicit val serialization = org.json4s.native.Serialization
  implicit val formats       = org.json4s.DefaultFormats

  val response: Identity[Response[Either[ResponseException[String, Exception], ResponsePayload]]] =
    basicRequest
      .post(uri"https://httpbin.org/post")
      .body(requestPayload)
      .response(asJson[ResponsePayload])
      .send(backend)

  println(s"Response:\n$response")
  line10.cyan pipe println

  response.body.fold(ex => println(ex), payload => println(s"Response payload:\n$payload"))

  line80.green pipe println
}
