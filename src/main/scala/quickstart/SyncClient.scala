package quickstart

import scala.util.chaining._
import util._

import sttp.client3.{basicRequest, SimpleHttpClient, UriContext}

object SyncClient extends App {

  line80.green pipe println

  val client   = SimpleHttpClient()
  val response = client.send(basicRequest.get(uri"https://httpbin.org/get"))
  println(response.body)

  line80.green pipe println
}
