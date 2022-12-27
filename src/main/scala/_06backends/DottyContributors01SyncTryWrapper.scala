package _06backends

import scala.util.Try
import sttp.client3._
import sttp.client3.circe._
import io.circe.generic.auto._
import cats.syntax.either._

object DottyContributors01SyncTryWrapper extends App {

  val user = "lampepfl"
  val repo = "dotty"

  val result =
    queryContributors(user, repo)
      .toEither
      .leftMap(_.toString)

  printQueryResult(user, repo, result)

  /** Query the GitHub API for contributors to a repository.
    * @param user
    *   the user or organization name
    * @param repo
    *   the repository name
    * @return
    *   a list of contributors wrapped in a 'Try' effect
    */
  def queryContributors(user: String, repo: String): Try[List[Contributor]] = {

    val uri = uri"https://api.github.com/repos/$user/$repo/contributors"

    val request: Request[List[Contributor], Any] =
      basicRequest
        .get(uri)
        .response(asJson[List[Contributor]].getRight)

    val backend: SttpBackend[Try, Any] =
      new TryBackend(HttpClientSyncBackend())

    val response: Try[Response[List[Contributor]]] =
      request.send(backend)

    response
      .map(_.body)
  }
}
