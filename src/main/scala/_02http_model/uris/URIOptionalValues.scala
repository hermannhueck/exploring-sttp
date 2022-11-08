package _02http_model.uris

import scala.util.chaining._
import util._

import sttp.client3._

object URIOptionalValues extends App {

  line80.green pipe println

  val v1 = None
  val v2 = Some("v2")

  println(uri"http://example.com?p1=$v1&p2=$v2")
  // http://example.com?p2=v2

  line10.cyan pipe println
  println(uri"http://$v1.$v2.example.com")
  // http://v2.example.com

  line10.cyan pipe println
  println(uri"http://example.com#$v1")
  // http://example.com

  line80.green pipe println
}
