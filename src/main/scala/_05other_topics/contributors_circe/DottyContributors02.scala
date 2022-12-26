package _05other_topics.contributors_circe

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.circe._
import io.circe._
import cats.implicits._ // for traverse and leftMap

object DottyContributors02 extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  val request: Request[Either[ResponseException[String, io.circe.Error], List[Json]], Any] =
    basicRequest
      .get(uri)
      .response(asJson[List[Json]])

  val response: Response[Either[ResponseException[String, io.circe.Error], List[Json]]] =
    SimpleHttpClient().send(request)

  val result: Either[String, List[Contributor]] = for {
    body         <- response.body.leftMap(_.toString)
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

  def parseBody(contributorsJson: List[Json]): Either[String, List[Contributor]] =
    contributorsJson
      .traverse(contributorJson2Contributor)
      .map { contributors =>
        contributors.sortBy(_.contributions).reverse
      }
      .leftMap(_.toString)

  def contributorJson2Contributor(contributorJson: Json): Either[Error, Contributor] =
    for {
      login         <- contributorJson.hcursor.downField("login").as[String]
      contributions <- contributorJson.hcursor.downField("contributions").as[Int]
    } yield Contributor(login, contributions)

  line80.green pipe println
}
