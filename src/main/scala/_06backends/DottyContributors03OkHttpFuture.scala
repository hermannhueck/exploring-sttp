package _06backends

import sttp.client3._
import sttp.client3.okhttp._
import sttp.client3.circe._
import io.circe.generic.auto._
import cats.syntax.either._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global

object DottyContributors03OkHttpFuture extends App {

  val user = "lampepfl"
  val repo = "dotty"

  val fResult = queryContributors(user, repo)
  fResult.onComplete { tryy =>
    printQueryResult(user, repo, tryy.toEither.leftMap(_.getMessage))
  }

  // wait for the Future to complete; otherwise you won't see the Future's output.
  Await.ready(fResult, 3.seconds)
  Thread.sleep(500)

  /** Query the GitHub API for the contributors of a repository.
    * @param user
    *   the user name
    * @param repo
    *   the repository name
    * @return
    *   a List of Contributors wrapped in a 'Future' effect
    */
  def queryContributors(user: String, repo: String): Future[List[Contributor]] = {

    val uri = uri"https://api.github.com/repos/$user/$repo/contributors"

    val request: Request[List[Contributor], Any] =
      basicRequest
        .get(uri)
        .response(asJson[List[Contributor]].getRight)

    val backend: SttpBackend[Future, Any] =
      OkHttpFutureBackend()

    val fResponse: Future[Response[List[Contributor]]] =
      request.send(backend)

    fResponse.map(_.body)
  }
}
