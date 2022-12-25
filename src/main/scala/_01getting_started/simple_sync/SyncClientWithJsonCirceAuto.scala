package _01getting_started.simple_sync

import scala.util.chaining._
import util._

import sttp.model.Uri
import sttp.client3.{basicRequest, Request, Response, ResponseException, SimpleHttpClient, UriContext}
import sttp.client3.circe._
// automatically converts between JSON and case classes or ADTs
import io.circe.generic.auto._

object SyncClientWithJsonCirceAuto extends App {

  line80.green pipe println

  val client: SimpleHttpClient = SimpleHttpClient()

  case class MyRequest(field1: String, field2: Int)
  // selected fields from the JSON that is being returned by httpbin
  case class HttpBinResponse(origin: String, headers: Map[String, String])

  val uri: Uri = uri"https://httpbin.org/post"

  val request: Request[Either[ResponseException[String, Exception], HttpBinResponse], Any] =
    basicRequest
      .post(uri)
      .body(MyRequest("test", 42))
      .response(asJson[HttpBinResponse])

  val response: Response[Either[ResponseException[String, Exception], HttpBinResponse]] =
    client.send(request) tap println

  s"$line10 response.code $line10".cyan pipe println
  response.code pipe println
  s"$line10 response.headers $line10".cyan pipe println
  response.headers pipe println
  s"$line10 response.body $line10".cyan pipe println
  response.body pipe println

  s"$line10 Final Result $line10".cyan pipe println
  response.body match {
    case Left(e)  => println(s"Got response exception:\n$e")
    case Right(r) => println(s"Origin's ip: ${r.origin}, header count: ${r.headers.size}")
  }

  line80.green pipe println
}
