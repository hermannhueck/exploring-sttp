package _05other_topics.jsonsupport_upickle

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.upicklejson._

object DottyContributors02a extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  implicit val responsePayloadRW: upickle.default.ReadWriter[ujson.Arr] =
    upickle.default.macroRW[ujson.Arr]

  val request: Request[Either[ResponseException[String, Exception], List[ujson.Value]], Any] =
    basicRequest
      .get(uri)
      .response(asJson[List[ujson.Value]])

  val backend: SttpBackend[Identity, Any]                                                 =
    HttpClientSyncBackend()
  val response: Response[Either[ResponseException[String, Exception], List[ujson.Value]]] =
    request.send(backend)

  import cats.implicits._

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

  def parseBody(contributorsJson: List[ujson.Value]): Either[String, List[Contributor]] =
    contributorsJson
      .traverse(contributorJson2Contributor)
      .map(_.sortBy(_.contributions).reverse)

  def contributorJson2Contributor(contributorJson: ujson.Value): Either[String, Contributor] = {
    import scala.util.Try
    val tryy: Try[Contributor] = for {
      login         <- Try(contributorJson("login").str)
      contributions <- Try(contributorJson("contributions").num.toInt)
    } yield Contributor(login, contributions)
    tryy.toEither.leftMap(_.toString)
  }

  line80.green pipe println
}
