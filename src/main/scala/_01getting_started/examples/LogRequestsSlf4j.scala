package _01getting_started.examples

import scala.util.chaining._
import util._

import io.circe.generic.auto._
import sttp.client3._
import sttp.client3.circe._
import sttp.client3.logging.slf4j.Slf4jLoggingBackend

object LogRequestsSlf4j extends App {

  line80.green pipe println

  case class HttpBinResponse(origin: String, headers: Map[String, String])

  val request: Request[HttpBinResponse, Any] =
    basicRequest
      .get(uri"https://httpbin.org/get")
      .response(asJson[HttpBinResponse].getRight)

  val backend: SttpBackend[Identity, Any] =
    Slf4jLoggingBackend(
      HttpClientSyncBackend(),
      includeTiming = true,
      logRequestBody = false,
      logResponseBody = false
    )

  try {
    val response: Response[HttpBinResponse] = request.send(backend)
    println("Done! " + response.code)
  } finally backend.close()

  line80.green pipe println
}
