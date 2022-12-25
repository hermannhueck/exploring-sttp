package _05other_topics.jsonsupport

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.sprayJson._
import spray.json._

object JsonSupportSprayJson extends App {

  import Model._

  line80.green pipe println

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  val requestPayload = RequestPayload("some data")

  implicit val payloadJsonFormat: RootJsonFormat[RequestPayload]     =
    new RootJsonFormat[RequestPayload] {
      def write(payload: RequestPayload): JsValue =
        JsObject("data" -> JsString(payload.data))
      def read(json: JsValue): RequestPayload     =
        json.asJsObject.getFields("data") match {
          case Seq(JsString(data)) => RequestPayload(data)
          case _                   => throw spray.json.DeserializationException("RequestPayload expected")
        }
    }
  implicit val myResponseJsonFormat: RootJsonFormat[ResponsePayload] =
    new RootJsonFormat[ResponsePayload] {
      def write(payload: ResponsePayload): JsValue =
        JsObject("data" -> JsString(payload.data))
      def read(json: JsValue): ResponsePayload     =
        json.asJsObject.getFields("data") match {
          case Seq(JsString(data)) => ResponsePayload(data)
          case _                   => throw spray.json.DeserializationException("ResponsePayload expected")
        }
    }

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
