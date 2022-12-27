package _06backends

import sttp.client3._
import sttp.client3.http4s.Http4sBackend
import sttp.capabilities.fs2.Fs2Streams
import cats.effect.{IO, IOApp}

object DottyContributors10Http4sStreaming extends IOApp.Simple {

  // implicit override val runtime: cats.effect.unsafe.IORuntime = cats.effect.unsafe.IORuntime.global
  import cats.effect.unsafe.implicits.global // this should not be needed in an IOApp!!!

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

    val request: Request[Either[String, fs2.Stream[IO, Byte]], Fs2Streams[IO]] =
      basicRequest
        .get(uri)
        .response(asStreamUnsafe(Fs2Streams[IO]))

    // The Http4sBackend can be used like the HttpClientCatsBackend (see the previous example),
    // but it can be used for streaming responses toojust as the HttpClientFs2Backend (as shown here).
    Http4sBackend.usingDefaultBlazeClientBuilder[IO]().use { backend: SttpBackend[IO, Fs2Streams[IO]] =>
      val responseIO: IO[Response[Either[String, fs2.Stream[IO, Byte]]]] =
        request.send(backend)

      responseIO.map { response =>
        response
          .body
          .flatMap { stream: fs2.Stream[IO, Byte] =>
            stream                           // : fs2.Stream[IO, Byte]
              .through(fs2.text.utf8.decode) // : fs2.Stream[IO, String]
              .compile
              .foldMonoid                    // : IO[String]
              .map(parseBody)                // : IO[Either[String, List[Contributor]]]
              .unsafeRunSync() // : Either[String, List[Contributor]]
          }
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
