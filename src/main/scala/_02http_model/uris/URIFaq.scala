package _02http_model.uris

import scala.util.chaining._
import util._

import sttp.model._
import sttp.client3._

object URIFaq extends App {

  line80.green pipe println

  println(uri"http://example.com?a=b/?c%26d".querySegmentsEncoding(Uri.QuerySegmentEncoding.All))
  // http://example.com?a=b%2F%3Fc%26d

  // compare to:
  println(uri"http://example.com?a=b/?c%26d")
  // http://example.com?a=b/?c%26d

  line80.green pipe println
}
