package _06backends

import sttp.client3._
import sttp.client3.http4s.Http4sBackend
import cats.effect.{IO, IOApp}
import sttp.client3.circe._
import io.circe.generic.auto._
import cats.syntax.either._

object DottyContributors10Http4s extends IOApp.Simple {

  val user = "lampepfl"
  val repo = "dotty"

  val run: IO[Unit] =
    queryContributors(user, repo)
      .map { result => printQueryResult(user, repo, result) }

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

    // The Http4sBackend can be used like the HttpClientCatsBackend,
    // but it can be used for streaming responses too just as the HttpClientFs2Backend (see the next example).
    Http4sBackend.usingDefaultBlazeClientBuilder[IO]().use { backend: SttpBackend[IO, Any] =>
      val responseIO: IO[Response[Either[ResponseException[String, io.circe.Error], List[Contributor]]]] =
        request.send(backend)
      responseIO.map { response =>
        response.body.leftMap(_.toString)
      }
    }
  }
}
