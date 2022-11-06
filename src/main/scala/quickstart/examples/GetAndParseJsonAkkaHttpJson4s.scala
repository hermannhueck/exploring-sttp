package quickstart.examples

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.akkahttp._
import sttp.client3.json4s._

import scala.concurrent._
// import scala.concurrent.duration._
import ExecutionContext.Implicits.global

object GetAndParseJsonAkkaHttpJson4s extends App {

  line80.green pipe println

  case class HttpBinResponse(origin: String, headers: Map[String, String])

  implicit val serialization = org.json4s.native.Serialization
  implicit val formats       = org.json4s.DefaultFormats

  // val request: RequestT[Identity, Either[ResponseException[String, Exception], HttpBinResponse], Any] =
  val request: Request[Either[ResponseException[String, Exception], HttpBinResponse], Any] =
    basicRequest
      .get(uri"https://httpbin.org/get")
      .response(asJson[HttpBinResponse])

  val backend: SttpBackend[Future, Any] = AkkaHttpBackend()

  val response: Future[Response[Either[ResponseException[String, Exception], HttpBinResponse]]] =
    request.send(backend)

  // Await.ready(response, 5.seconds)

  for {
    r <- response
  } {
    println(s"Got response code: ${r.code}")
    println(r.body)
    backend.close()
    line80.green pipe println
  }
}
