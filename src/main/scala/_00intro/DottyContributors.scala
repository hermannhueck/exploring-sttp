package _00intro

import scala.util.chaining._
import util._

import sttp.client3._

object DottyContributors extends App {

  line80.green pipe println

  val repo = "lampepfl/dotty"
  val uri  = uri"https://api.github.com/repos/$repo/contributors"

  val request: Request[Either[String, String], Any] = basicRequest.get(uri)

  val backend: SttpBackend[Identity, Any]        = HttpClientSyncBackend()
  val response: Response[Either[String, String]] = request.send(backend)

  // response.body match {
  //   case Left(requestError) =>
  //     println(s"HTTP request error: $requestError")
  //   case Right(body)        =>
  //     parseBody(body) match {
  //       case Left(parseError)    =>
  //         println(s"JSON parse error: $parseError")
  //       case Right(contributors) =>
  //         contributors.foreach { case login -> contributions =>
  //           println(s"Contributor ${login} made ${contributions} contributions")
  //         }
  //     }
  // }

  // 'Serializable' is the common supertype of 'Error' and 'String'
  val result: Either[Serializable, List[(String, Int)]] = for {
    body         <- response.body
    contributors <- parseBody(body)
  } yield contributors

  result match {
    case Left(error)         =>
      println(s"HTTP request error or JSON parse error: $error")
    case Right(contributors) =>
      contributors.foreach { case login -> contributions =>
        println(s"Contributor ${login} made ${contributions} contributions")
      }
  }

  import io.circe._
  import cats.implicits._

  // parse body as JSON
  def parseBody(body: String): Either[Error, List[(String, Int)]] =
    for {
      json          <- io.circe.parser.parse(body)
      contributors  <- json.hcursor.as[List[Json]]
      logins        <- contributors.traverse { contributor =>
                         contributor.hcursor.downField("login").as[String]
                       }
      contributions <- contributors.traverse { count =>
                         count.hcursor.downField("contributions").as[Int]
                       }
    } yield logins
      .zip(contributions)
      .sortBy(_._2)
      .reverse

  line80.green pipe println
}
