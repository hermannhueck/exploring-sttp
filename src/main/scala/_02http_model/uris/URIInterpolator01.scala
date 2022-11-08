package _02http_model.uris

import scala.util.chaining._
import util._
import munit.Assertions._

import sttp.client3._
import sttp.model._

object URIInterpolator01 extends App with HeaderNames with MediaTypes with StatusCodes {

  line80 pipe println

  val user   = "Mary Smith"
  val filter = "programming languages"

  val endpoint: Uri = uri"http://example.com/$user/skills?filter=$filter" tap println

  val expected = "http://example.com/Mary%20Smith/skills?filter=programming+languages"
  assertEquals(endpoint.toString, expected)
  s"assertion is ${endpoint.toString == expected}" pipe println

  line80 pipe println
}
