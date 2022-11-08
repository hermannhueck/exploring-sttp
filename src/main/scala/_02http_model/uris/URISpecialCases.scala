package _02http_model.uris

import scala.util.chaining._
import util._

import sttp.client3._

object URISpecialCases extends App {

  line80.green pipe println

  val endpoint = "http://example.com/api"

  println(uri"$endpoint/login")
  // http://example.com/api/login

  line80.green pipe println
}
