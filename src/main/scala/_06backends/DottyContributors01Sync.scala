package _06backends

import sttp.client3._
import sttp.client3.circe._
import io.circe.generic.auto._
import cats.syntax.either._

object DottyContributors01Sync extends App {

  val user = "lampepfl"
  val repo = "dotty"

  val result = queryContributors(user, repo)
  printQueryResult(user, repo, result)

  /** Query the GitHub API for contributors to a repository.
    * @param user
    *   the user or organization name
    * @param repo
    *   the repository name
    * @return
    *   a list of contributors or an error message. In this example the is wrapped in an 'Identity' effect which is no
    *   effect at all. It is just a type alias for 'Id' which is an alias for 'A' itself. 'type Identity[A] = A'
    */
  def queryContributors(user: String, repo: String): Identity[Either[String, List[Contributor]]] = {

    val uri = uri"https://api.github.com/repos/$user/$repo/contributors"

    val request: Request[Either[ResponseException[String, io.circe.Error], List[Contributor]], Any] =
      basicRequest
        .get(uri)
        .response(asJson[List[Contributor]])

    val backend: SttpBackend[Identity, Any] =
      HttpClientSyncBackend()

    val response: Identity[Response[Either[ResponseException[String, io.circe.Error], List[Contributor]]]] =
      request.send(backend)

    response.body.leftMap(_.toString)
  }
}
