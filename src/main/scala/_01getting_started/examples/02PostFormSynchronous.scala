package _01getting_started.examples

import scala.util.chaining._
import util._

import sttp.client3._

object PostFormSynchronous extends App {

  line80.green pipe println

  val signup = Some("yes")

  val request: Request[Either[String, String], Any] =
    basicRequest
      // send the body as form data (x-www-form-urlencoded)
      .body(Map("name" -> "John", "surname" -> "doe"))
      // use an optional parameter in the URI
      .post(uri"https://httpbin.org/post?signup=$signup")

  val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  val response: Identity[Response[Either[String, String]]] = request.send(backend)

  println(s"${line(5)} response.body ${line(5)}".cyan)
  println(response.body)
  println(s"${line(5)} response.headers ${line(5)}".cyan)
  println(response.headers)

  line80.green pipe println
}
