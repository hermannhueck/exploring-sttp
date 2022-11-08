package _02http_model.uris

import scala.util.chaining._
import util._

import sttp.client3._

object URIInterpolator02 extends App {

  line80.green pipe println

  s"the embedded / is escaped" pipe println
  println(uri"http://example.org/${"a/b"}")
  // http://example.org/a%2Fb

  line10.cyan pipe println
  s"the embedded / is not escaped" pipe println
  println(uri"http://example.org/${"a"}/${"b"}")
  // http://example.org/a/b

  line10.cyan pipe println
  s"the embedded : is not escaped" pipe println
  println(uri"http://${"example.org:8080"}")
  // http://example.org:8080

  line80.green pipe println
}
