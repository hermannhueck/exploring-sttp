package _06backends

import sttp.client3._
import cats.effect.IO
import sttp.client3.httpclient.cats.HttpClientCatsBackend
import sttp.client3.circe._
import io.circe.generic.auto._
import cats.syntax.either._

object DottyContributors06CatsEffect3 extends App {

  implicit val runtime: cats.effect.unsafe.IORuntime = cats.effect.unsafe.IORuntime.global

  val user = "lampepfl"
  val repo = "dotty"

  queryContributors(user, repo)
    .map { result => printQueryResult(user, repo, result) }
    .unsafeRunSync()

  /** Query the GitHub API for contributors to a repository.
    * @param user
    *   the user or organization name
    * @param repo
    *   the repository name
    * @return
    *   a list of contributors or an error message wrapped in an 'IO' effect
    */
  def queryContributors(user: String, repo: String): IO[Either[String, List[Contributor]]] = {

    val uri = uri"https://api.github.com/repos/$user/$repo/contributors"

    val request: Request[Either[ResponseException[String, io.circe.Error], List[Contributor]], Any] =
      basicRequest
        .get(uri)
        .response(asJson[List[Contributor]])

    HttpClientCatsBackend.resource[IO]().use { backend: SttpBackend[IO, Any] =>
      val responseIO: IO[Response[Either[ResponseException[String, io.circe.Error], List[Contributor]]]] =
        request.send(backend)
      responseIO.map { response =>
        response.body.leftMap(_.toString)
      }
    }
  }
}
