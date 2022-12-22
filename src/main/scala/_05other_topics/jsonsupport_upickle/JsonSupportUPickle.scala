package _05other_topics.jsonsupport_upickle

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.upicklejson._
import upickle.default._

object JsonSupportUPickle extends App {

  import Model._

  line80.green pipe println

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  val requestPayload = RequestPayload("some data")

  implicit val requestPayloadRW: ReadWriter[RequestPayload]   = macroRW[RequestPayload]
  implicit val responsePayloadRW: ReadWriter[ResponsePayload] = macroRW[ResponsePayload]

  val response: Response[Either[ResponseException[String, Exception], ResponsePayload]] =
    basicRequest                           // : RequestT[Empty, Either[String, String], Any]
      .post(uri"https://httpbin.org/post") // : Request[Either[String, String], Any]
      .body(requestPayload)                // : Request[Either[String, String], Any]
      .response(asJson[ResponsePayload])
      // : Request[Either[ResponseException[String, Exception], ResponsePayload], Any]
      .send(backend)                       // : Response[Either[ResponseException[String, Exception], ResponsePayload]]

  // println(s"Response:\n$response")
  // line10.cyan pipe println

  // response.body.fold(ex => println(ex), payload => println(s"Response payload:\n$payload"))

  line80.green pipe println
}
