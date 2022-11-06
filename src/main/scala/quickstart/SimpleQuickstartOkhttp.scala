package quickstart

import scala.util.chaining._
import util._

import sttp.client3.okhttp.quick._

object SimpleQuickstartOkhttp extends App {

  line80.green pipe println

  quickRequest.get(uri"http://httpbin.org/ip").send(backend) pipe println

  line80.green pipe println
}
