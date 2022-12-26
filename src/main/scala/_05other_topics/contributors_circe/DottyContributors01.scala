package _05other_topics.contributors_circe

import scala.util.chaining._
import util._

import sttp.client3._

object DottyContributors01 extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  val request: Request[Either[String, String], Any] =
    basicRequest.get(uri)

  val response: Response[Either[String, String]] =
    SimpleHttpClient().send(request)

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
      printMostBusyContributor(s"$user/$repo", contributors)
  }

  import io.circe._
  import cats.implicits._ // for traverse and leftMap

  def parseBody(body: String): Either[String, List[Contributor]] = {
    for {
      json             <- io.circe.parser.parse(body)
      contributorsJson <- json.hcursor.as[List[Json]]
      contributors     <- contributorsJson.traverse(contributorJson2Contributor)
    } yield contributors
      .sortBy(_.contributions)
      .reverse
  }.leftMap(_.toString)

  def contributorJson2Contributor(contributorJson: Json): Either[Error, Contributor] =
    for {
      login         <- contributorJson.hcursor.downField("login").as[String]
      contributions <- contributorJson.hcursor.downField("contributions").as[Int]
    } yield Contributor(login, contributions)

  line80.green pipe println
}
