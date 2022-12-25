package _05other_topics.contributors_circe

import scala.util.chaining._
import util._

import sttp.client3._

object DottyContributors01 extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  final case class Contributor(login: String, contributions: Int)

  val request: Request[Either[String, String], Any] = basicRequest.get(uri)

  val backend: SttpBackend[Identity, Any]        =
    HttpClientSyncBackend()
  val response: Response[Either[String, String]] =
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

  import io.circe._
  import cats.implicits._

  // parse body as JSON
  def parseBody(body: String): Either[Error, List[Contributor]] =
    for {
      json             <- io.circe.parser.parse(body)
      contributorsJson <- json.hcursor.as[List[Json]]
      contributors     <- contributorsJson.traverse(contributorJson2Contributor)
    } yield contributors
      .sortBy(_.contributions)
      .reverse

  def contributorJson2Contributor(contributorJson: Json): Either[Error, Contributor] =
    for {
      login         <- contributorJson.hcursor.downField("login").as[String]
      contributions <- contributorJson.hcursor.downField("contributions").as[Int]
    } yield Contributor(login, contributions)

  line80.green pipe println
}
