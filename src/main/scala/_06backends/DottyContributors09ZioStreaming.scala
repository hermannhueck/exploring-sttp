package _06backends

import sttp.client3._
import sttp.capabilities.zio.ZioStreams
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio._
import zio.stream._

object DottyContributors09ZioStreaming extends ZIOAppDefault {

  val user = "lampepfl"
  val repo = "dotty"

  override val run: Task[Unit] =
    queryContributors(user, repo)
      .map { result => printQueryResult(user, repo, result) }

  /** Query the GitHub API for contributors to a repository.
    * @param user
    *   the user or organization name
    * @param repo
    *   the repository name
    * @return
    *   a list of contributors or an error message wrapped in an 'zio.Task' effect
    */
  def queryContributors(user: String, repo: String): Task[Either[String, List[Contributor]]] = {

    val uri = uri"https://api.github.com/repos/$user/$repo/contributors"

    val request = // : Request[String, Any with sttp.capabilities.Effect[ZIO[Any, Throwable, String]] with ZioStreams] =
      basicRequest
        .get(uri)
        .response(asStream(ZioStreams)(_.via(ZPipeline.utf8Decode).runFold("")(_ + _)))

    HttpClientZioBackend().flatMap { backend: SttpBackend[Task, ZioStreams] =>
      val responseTask: Task[Response[Either[String, String]]] =
        request.send(backend)

      responseTask
        .map(_.body)
        .flatMap { errOrBody: Either[String, String] =>
          ZIO.fromEither { Right(errOrBody.flatMap(parseBody)).withLeft[Throwable] }
        }
    }
  }

  import io.circe._
  import cats.implicits._ // for traverse and leftMap

  def parseBody(body: String): Either[String, List[Contributor]] = {
    for {
      json             <- io.circe.parser.parse(body)
      contributorsJson <- json.hcursor.as[List[Json]]
      contributors     <- contributorsJson.traverse(contributorJson2Contributor)
    } yield contributors
      .sortBy(_.contributions)
      .reverse
  }.leftMap(_.toString)

  def contributorJson2Contributor(contributorJson: Json): Either[Error, Contributor] =
    for {
      login         <- contributorJson.hcursor.downField("login").as[String]
      contributions <- contributorJson.hcursor.downField("contributions").as[Int]
    } yield Contributor(login, contributions)
}
