package _02http_model.uris

import scala.util.chaining._
import util._

import sttp.client3._

object URIMapsSequences extends App {

  line80.green pipe println

  val ps = Map("p1" -> "v1", "p2" -> "v2")

  println(uri"http://example.com?$ps&p3=p4")
  // http://example.com?p1=v1&p2=v2&p3=p4

  val params = List("a", "b", "c")

  println(uri"http://example.com/$params")
  // http://example.com/a/b/c

  line80.green pipe println
}
