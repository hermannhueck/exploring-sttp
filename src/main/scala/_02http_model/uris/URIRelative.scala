package _02http_model.uris

import scala.util.chaining._
import util._

import sttp.client3._

object URIRelative extends App {

  line80.green pipe println

  val params = List("a", "b", "c")

  println(uri"/api/$params")
  // /api/a/b/c

  line80.green pipe println
}
