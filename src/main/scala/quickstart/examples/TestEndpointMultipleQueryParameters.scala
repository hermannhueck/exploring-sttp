package quickstart.examples

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.testing._

object TestEndpointMultipleQueryParameters extends App {

  line80.green pipe println

  val backend =
    SttpBackendStub
      .synchronous
      .whenRequestMatches(_.uri.paramsMap.contains("filter"))
      .thenRespond("Filtered")
      .whenRequestMatches(_.uri.path.contains("secret"))
      .thenRespond("42")

  val parameters1 = Map("filter" -> "name=mary", "sort" -> "asc")

  println(
    basicRequest
      .get(uri"http://example.org?search=true&$parameters1")
      .send(backend)
      .body
  )

  val parameters2 = Map("sort" -> "desc")

  println(
    basicRequest
      .get(uri"http://example.org/secret/read?$parameters2")
      .send(backend)
      .body
  )

  line80.green pipe println
}
