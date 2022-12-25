package _01getting_started.quickstart

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.model.Uri

object SimpleQuickstartBasic extends App {

  line80.green pipe println

  val uri: Uri = uri"https://httpbin.org/post?hello=world"

  val request: Request[Either[String, String], Any] =
    basicRequest
      .body("Hello, world!")
      .post(uri)
  val backend: SttpBackend[Identity, Any]           = HttpClientSyncBackend()
  val response: Response[Either[String, String]]    = request.send(backend) tap println

  s"$line10 StatusCode $line10".cyan pipe println
  response.code pipe println
  s"$line10 Headers $line10".cyan pipe println
  response.headers pipe println
  s"$line10 Body $line10".cyan pipe println
  response.body pipe println

  line80.green pipe println
}
