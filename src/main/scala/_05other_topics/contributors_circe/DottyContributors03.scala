package _05other_topics.contributors_circe

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.circe._
import io.circe.generic.auto._

object DottyContributors03 extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  final case class Contributor(login: String, contributions: Int)

  val request: Request[Either[ResponseException[String, io.circe.Error], List[Contributor]], Any] =
    basicRequest
      .get(uri)
      .response(asJson[List[Contributor]])

  val backend: SttpBackend[Identity, Any]                                                      =
    HttpClientSyncBackend()
  val response: Response[Either[ResponseException[String, io.circe.Error], List[Contributor]]] =
    request.send(backend)

  response.body match {
    case Left(error)         =>
      println(s"HTTP request error or JSON parse error: $error".red)
    case Right(contributors) =>
      // printContributors(s"$user/$repo", contributors)
      printContributorsSummary(s"$user/$repo", contributors.size, contributors.map(_.contributions).sum)
  }

  line80.green pipe println
}
