package _05other_topics.jsonsupport

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.ziojson._
import zio.json._

object JsonSupportZioJson extends App {

  import Model._

  line80.green pipe println

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  val requestPayload = RequestPayload("some data")

  implicit val payloadJsonEncoder: JsonEncoder[RequestPayload]     = DeriveJsonEncoder.gen[RequestPayload]
  implicit val myResponseJsonDecoder: JsonDecoder[ResponsePayload] = DeriveJsonDecoder.gen[ResponsePayload]

  val response: Identity[Response[Either[ResponseException[String, String], ResponsePayload]]] =
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
