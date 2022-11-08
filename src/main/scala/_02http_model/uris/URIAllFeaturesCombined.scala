package _02http_model.uris

import scala.util.chaining._
import util._
import munit.Assertions._

import sttp.client3._

object URIAllFeaturesCombined extends App {

  line80.green pipe println

  val secure     = true
  val scheme     = if (secure) "https" else "http"
  val subdomains = List("sub1", "sub2")
  val vx         = Some("y z")
  val paramMap   = Map("a" -> 1, "b" -> 2)
  val jumpTo     = Some("section2")

  val uri = uri"$scheme://$subdomains.example.com/api/$vx?$paramMap#$jumpTo"

  println(uri)
  // https://sub1.sub2.example.com?x=y+z&a=1&b=2#section2

  val expected = "https://sub1.sub2.example.com/api/y%20z?a=1&b=2#section2"
  assertEquals(uri.toString, expected)
  s"assertion is ${uri.toString == expected}" pipe println

  line80.green pipe println
}
