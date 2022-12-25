package _01getting_started.quickstart

import scala.util.chaining._
import util._

import sttp.model.Uri
import sttp.client3.{Request, Response}
import sttp.client3.okhttp.quick._

object SimpleQuickstartOkhttp extends App {

  line80.green pipe println

  val uri: Uri = uri"https://httpbin.org/ip"

  val request: Request[String, Any] = quickRequest.get(uri)

  val response: Response[String] = request.send(backend) tap println

  s"$line10 StatusCode $line10".cyan pipe println
  response.code pipe println
  s"$line10 Headers $line10".cyan pipe println
  response.headers pipe println
  s"$line10 Body $line10".cyan pipe println
  response.body pipe println

  line80.green pipe println
}
