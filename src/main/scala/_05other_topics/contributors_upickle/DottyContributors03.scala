package _05other_topics.contributors_upickle

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.upicklejson._

object DottyContributors03 extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  implicit val contributorRW: upickle.default.ReadWriter[Contributor] =
    upickle.default.macroRW[Contributor]

  val request: Request[Either[ResponseException[String, Exception], List[Contributor]], Any] =
    basicRequest
      .get(uri)
      .response(asJson[List[Contributor]])

  val response: Response[Either[ResponseException[String, Exception], List[Contributor]]] =
    SimpleHttpClient().send(request)

  response.body match {
    case Left(error)         =>
      println(s"HTTP request error or JSON parse error: $error".red)
    case Right(contributors) =>
      // printContributors(s"$user/$repo", contributors)
      printContributorsSummary(s"$user/$repo", contributors.size, contributors.map(_.contributions).sum)
      printMostBusyContributor(s"$user/$repo", contributors)
  }

  line80.green pipe println
}
