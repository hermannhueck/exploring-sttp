package quickstart

import scala.util.chaining._
import util._

import sttp.client3.{basicRequest, SimpleHttpClient, UriContext}
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import sttp.client3.upicklejson._
import upickle.default._

object SyncClientWithJsonWithLogging extends App {

  line80.green pipe println

  val client = SimpleHttpClient().wrapBackend(Slf4jLoggingBackend(_))

  case class MyRequest(field1: String, field2: Int)
  // selected fields from the JSON that is being returned by httpbin
  case class HttpBinResponse(origin: String, headers: Map[String, String])

  implicit val myRequestRW: ReadWriter[MyRequest]      = macroRW[MyRequest]
  implicit val responseRW: ReadWriter[HttpBinResponse] = macroRW[HttpBinResponse]

  val request  = basicRequest
    .post(uri"https://httpbin.org/post")
    .body(MyRequest("test", 42))
    .response(asJson[HttpBinResponse])
  val response = client.send(request)

  response.body match {
    case Left(e)  => println(s"Got response exception:\n$e")
    case Right(r) => println(s"Origin's ip: ${r.origin}, header count: ${r.headers.size}")
  }

  line80.green pipe println
}
