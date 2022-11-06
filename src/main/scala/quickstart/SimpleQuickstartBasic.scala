package quickstart

import scala.util.chaining._
import util._

import sttp.client3._

object SimpleQuickstartBasic extends App {

  line80.green pipe println

  val backend  = HttpClientSyncBackend()
  val response = basicRequest
    .body("Hello, world!")
    .post(uri"https://httpbin.org/post?hello=world")
    .send(backend)

  println(response.body)

  line80.green pipe println
}
