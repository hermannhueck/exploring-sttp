package _00intro

import scala.util.chaining._
import util._

import sttp.client3._
import io.circe._
import cats.implicits._

object SttpIntroParseJson extends App {

  line80.green pipe println

  val query = "dotty"

  // the `query` parameter is automatically url-encoded
  // `sort` is removed, as the value is not defined
  val request: Request[Either[String, String], Any] =
    basicRequest.get(uri"https://api.github.com/search/repositories?q=$query")

  val backend: SttpBackend[Identity, Any]        = HttpClientSyncBackend()
  val response: Response[Either[String, String]] = request.send(backend)

  // parse the body as JSON
  for {
    body      <- response.body
    json      <- io.circe.parser.parse(body)
    items     <- json.hcursor.downField("items").as[List[Json]]
    fullNames <- items.traverse { item =>
                   item.hcursor.downField("full_name").as[String]
                 }
    urls      <- items.traverse { item =>
                   item.hcursor.downField("html_url").as[String]
                 }
  } (fullNames, urls).pipe { case (fullNames, urls) =>
    fullNames.zip(urls).foreach { case (fullName, url) =>
      println(s"$fullName: $url")
    }
  }

  line80.green pipe println
}
