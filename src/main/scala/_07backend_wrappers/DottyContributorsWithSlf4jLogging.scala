package _07backend_wrappers

import sttp.client3._
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import sttp.client3.circe._
import io.circe.generic.auto._
import cats.syntax.either._

object DottyContributorsWithSlf4jLogging extends App {

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
      Slf4jLoggingBackend(
        HttpClientSyncBackend(),
        includeTiming = true,
        beforeCurlInsteadOfShow = false,
        logRequestBody = false,
        logRequestHeaders = false,
        logResponseBody = false,
        logResponseHeaders = false
      )

    val response: Identity[Response[Either[ResponseException[String, io.circe.Error], List[Contributor]]]] =
      request.send(backend)

    response.body.leftMap(_.toString)
  }
}
