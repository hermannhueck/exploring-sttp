package quickstart.examples

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio.{durationInt, Schedule, Task, ZIO, ZIOAppDefault}

object RetryZio extends ZIOAppDefault {

  line80.green pipe println

  override def run: ZIO[Any, Throwable, Response[String]] = {

    HttpClientZioBackend()
      .flatMap { backend =>
        val localhostRequest: Request[String, Any] =
          basicRequest
            .get(uri"http://localhost/test")
            .response(asStringAlways)

        val sendWithRetries: Task[Response[String]] =
          localhostRequest
            .send(backend)
            .either
            .repeat(
              Schedule.spaced(1.second) *>
                Schedule.recurs(10) *>
                Schedule.recurWhile(result => RetryWhen.Default(localhostRequest, result))
            )
            .absolve

        sendWithRetries.ensuring(backend.close().ignore)
      }
  }
}
