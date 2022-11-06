package quickstart

import scala.util.chaining._
import util._

import sttp.client3._

object SttpIntro extends App {

  line80.green pipe println

  val query                = "http language:scala"
  val sort: Option[String] = None

  // the `query` parameter is automatically url-encoded
  // `sort` is removed, as the value is not defined
  val request = basicRequest.get(uri"https://api.github.com/search/repositories?q=$query&sort=$sort")

  val backend  = HttpClientSyncBackend()
  val response = request.send(backend)

  // response.header(...): Option[String]
  println(response.header("Content-Length"))

  // response.body: by default read into an Either[String, String]
  // to indicate failure or success
  println(response.body)

  line80.green pipe println
}
