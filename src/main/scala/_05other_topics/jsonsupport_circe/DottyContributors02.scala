package _05other_topics.jsonsupport_circe

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.circe._
import io.circe._

object DottyContributors02 extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  final case class Contributor(login: String, contributions: Int)

  val request: Request[Either[ResponseException[String, io.circe.Error], List[Json]], Any] =
    basicRequest
      .get(uri)
      .response(asJson[List[Json]])

  val backend: SttpBackend[Identity, Any]                                               =
    HttpClientSyncBackend()
  val response: Response[Either[ResponseException[String, io.circe.Error], List[Json]]] =
    request.send(backend)

  // println(s"Response:\n$response")
  // line10.cyan pipe println

  // 'Serializable' is the common supertype of 'Error' and 'String'
  val result: Either[Serializable, List[Contributor]] = for {
    body         <- response.body
    contributors <- parseBody(body)
  } yield contributors

  result match {
    case Left(error)         =>
      println(s"HTTP request error or JSON parse error: $error".red)
    case Right(contributors) =>
      // printContributors(s"$user/$repo", contributors)
      printContributorsSummary(s"$user/$repo", contributors.size, contributors.map(_.contributions).sum)
  }

  import cats.implicits._

  // parse body as JSON
  def parseBody(contributorsJson: List[Json]) = // : Either[Error, List[Contributor]] =
    contributorsJson
      .traverse(contributorJson2Contributor)
      .map { contributors =>
        contributors.sortBy(_.contributions).reverse
      }

  def contributorJson2Contributor(contributorJson: Json): Either[Error, Contributor] =
    for {
      login         <- contributorJson.hcursor.downField("login").as[String]
      contributions <- contributorJson.hcursor.downField("contributions").as[Int]
    } yield Contributor(login, contributions)

  line80.green pipe println
}
