package _01getting_started.examples

import util._

import io.circe.generic.auto._
import sttp.client3._
import sttp.client3.circe._
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio._

object GetAndParseJsonZioCirce extends ZIOAppDefault {

  override def run = {

    case class HttpBinResponse(origin: String, headers: Map[String, String])

    val request: Request[Either[ResponseException[String, io.circe.Error], HttpBinResponse], Any] =
      basicRequest
        .get(uri"https://httpbin.org/get")
        .response(asJson[HttpBinResponse])

    // create a description of a program, which requires SttpClient dependency in the environment
    def sendAndPrint(backend: SttpBackend[Task, Any]): Task[Unit] = for {
      _        <- Console.printLine(line80.green)
      response <- backend.send(request)
      _        <- Console.printLine(s"Got response code: ${response.code}")
      _        <- Console.printLine(response.body.toString)
      _        <- Console.printLine(line80.green)
    } yield ()

    // provide an implementation for the SttpClient dependency
    HttpClientZioBackend
      .scoped()
      .flatMap(sendAndPrint)
  }
}
