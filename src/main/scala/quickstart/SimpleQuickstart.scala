package quickstart

import scala.util.chaining._
import util._

import sttp.client3.quick._

object SimpleQuickstart extends App {

  line80.green pipe println

  simpleHttpClient.send(quickRequest.get(uri"http://httpbin.org/ip")) pipe println

  line80.green pipe println
}
