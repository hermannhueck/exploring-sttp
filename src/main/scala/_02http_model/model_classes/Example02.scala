package _02http_model.model_classes

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.model._

object Example02 extends App with HeaderNames with MediaTypes with StatusCodes {

  line80 pipe println

  val request =
    basicRequest
      .header(ContentType, ApplicationJson.toString)
      .get(uri"https://httpbin.org")

  val backend  = HttpClientSyncBackend()
  val response = request.send(backend)

  s"Status: ${response.code}" pipe println
  if (response.code == StatusCode.Ok) println("Ok!")

  line80 pipe println
}
