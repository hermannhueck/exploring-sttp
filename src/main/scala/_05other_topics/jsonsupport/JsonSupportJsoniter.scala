package _05other_topics.jsonsupport

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.jsoniter._
import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros._

object JsonSupportJsoniter extends App {

  import Model._

  line80.green pipe println

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  val requestPayload = RequestPayload("some data")

  implicit val payloadJsonCodec: JsonValueCodec[RequestPayload]   = JsonCodecMaker.make
  // note that the jsoniter doesn't support 'implicit defs' and so either has to be generated seperatly
  implicit val jsonEitherDecoder: JsonValueCodec[ResponsePayload] = JsonCodecMaker.make

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
