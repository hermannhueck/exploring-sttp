package _01getting_started.simple_sync

import scala.util.chaining._
import util._

import sttp.model.Uri
import sttp.client3.{basicRequest, Request, Response, SimpleHttpClient, UriContext}

object SyncClient extends App {

  line80.green pipe println

  val uri: Uri                                      = uri"https://httpbin.org/get?hello=world"
  val request: Request[Either[String, String], Any] = basicRequest.get(uri)

  val client: SimpleHttpClient                   = SimpleHttpClient()
  val response: Response[Either[String, String]] = client.send(request) tap println

  s"$line10 response.code $line10".cyan pipe println
  response.code pipe println
  s"$line10 response.headers $line10".cyan pipe println
  response.headers pipe println
  s"$line10 response.body $line10".cyan pipe println
  response.body pipe println

  s"$line10 Final Result $line10".cyan pipe println
  response.body match {
    case Left(error) =>
      println(s"Got response ERROR:\n$error")
    case Right(body) =>
      println(s"BODY:\n${body}")
      s"$line10 Origin IP $line10".cyan pipe println
  }

  line80.green pipe println
}
