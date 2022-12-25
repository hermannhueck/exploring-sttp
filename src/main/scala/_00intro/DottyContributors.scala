package _00intro

import scala.util.chaining._
import util._

import sttp.client3._
import cats.implicits._

object DottyContributors extends App {

  line80.green pipe println

  val user = "lampepfl"
  val repo = "dotty"
  val uri  = uri"https://api.github.com/repos/$user/$repo/contributors"

  val request: Request[Either[String, String], Any] = basicRequest.get(uri)

  // val backend: SttpBackend[Identity, Any]        = HttpClientSyncBackend()
  // val response: Response[Either[String, String]] = request.send(backend)
  // is the same as:
  // val response: Response[Either[String, String]] = SimpleHttpClient().send(request)
  // is the same as:
  val response: Response[Either[String, String]] = quick.simpleHttpClient.send(request)

  val result: Either[String, List[(String, Int)]] =
    response.body.flatMap(parseBody).leftMap(_.toString)

  result match {
    case Left(error)         =>
      println(s"HTTP request error or JSON parse error: $error".red)
    case Right(contributors) =>
      printContributors(contributors)
  }

  def printContributors(contributors: List[(String, Int)]): Unit = {
    // s"$line5 Contributors of repo $user/$repo $line10".cyan pipe println
    // contributors.foreach { case login -> contributions =>
    //   println(s"Contributor ${login} made ${contributions} contributions")
    // }
    s"Repo $repo has ${contributors.size} contributors who made ${contributors.map(_._2).sum} contributions in total.".cyan pipe println
    val mostBusyContributor = contributors.maxBy(_._2)
    s"The most busy contributor of repo $repo is '${mostBusyContributor._1}' with ${mostBusyContributor._2} contributions.".cyan pipe println
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
