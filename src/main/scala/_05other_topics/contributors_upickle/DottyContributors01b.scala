package _05other_topics.contributors_upickle

import scala.util.chaining._
import util._

import sttp.client3._

object DottyContributors01b extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  val request: Request[Either[String, String], Any] =
    basicRequest.get(uri)

  val response: Response[Either[String, String]] =
    SimpleHttpClient().send(request)

  val result: Either[String, List[Contributor]] = for {
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

  import cats.implicits._
  import scala.util.Try

  def parseBody(body: String): Either[String, List[Contributor]] = {
    implicit val contributorRW: upickle.default.ReadWriter[Contributor] =
      upickle.default.macroRW[Contributor]
    Try {
      val contributors: List[Contributor] =
        upickle.default.read[List[Contributor]](body)
      contributors.sortBy(_.contributions).reverse
    }.toEither.leftMap(_.toString)
  }

  line80.green pipe println
}
