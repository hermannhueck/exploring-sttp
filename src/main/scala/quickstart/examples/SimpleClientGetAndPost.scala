package quickstart.examples

import scala.util.chaining._
import util._

import sttp.client3.{asStringAlways, basicRequest, Request, Response, SimpleHttpClient, UriContext}

import java.util.UUID

object SimpleClientGetAndPost extends App {

  line80.green pipe println

  val client = SimpleHttpClient()

  val request1: Request[Either[String, String], Any] =
    basicRequest
      .get(uri"https://httpbin.org/get")

  try {
    val response1: Response[Either[String, String]] =
      client.send(request1)

    response1.body match {
      case Left(body)  => println(s"Non-2xx response to GET with code ${response1.code}:\n$body")
      case Right(body) => println(s"2xx response to GET:\n$body")
    }

    line(20).cyan pipe println

    //

    val request2: Request[String, Any] =
      basicRequest
        .header("X-Correlation-ID", UUID.randomUUID().toString)
        .response(asStringAlways)
        .body("Hello, world!")
        .post(uri"https://httpbin.org/post")

    val response2: Response[String] = client.send(request2)

    println(s"Response to POST:\n${response2.body}")

  } finally client.close()

  line80.green pipe println
}
