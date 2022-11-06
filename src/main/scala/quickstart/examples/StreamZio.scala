package quickstart.examples

import scala.util.chaining._
import util._

import sttp.capabilities.zio.ZioStreams
import sttp.client3._
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio.Console._
import zio._
import zio.stream._

object StreamZio extends ZIOAppDefault {

  line80.green pipe println

  def streamRequestBody(backend: SttpBackend[Task, ZioStreams]): Task[Unit] = {

    val stream: Stream[Throwable, Byte] = ZStream("Hello, world".getBytes.toIndexedSeq: _*)

    backend
      .send(
        basicRequest
          .streamBody(ZioStreams)(stream)
          .post(uri"https://httpbin.org/post")
      )
      .flatMap { response => printLine(s"RECEIVED:\n${response.body}\n${line(20).cyan}") }
  }

  def streamResponseBody(backend: SttpBackend[Task, ZioStreams]): Task[Unit] = {
    backend
      .send(
        basicRequest
          .body("I want a stream!")
          .post(uri"https://httpbin.org/post")
          .response(asStreamAlways(ZioStreams)(_.via(ZPipeline.utf8Decode).runFold("")(_ + _)))
      )
      .flatMap { response => printLine(s"RECEIVED:\n${response.body}\n${line80.green}") }
  }

  override def run =
    HttpClientZioBackend.scoped().flatMap { backend =>
      streamRequestBody(backend) *> streamResponseBody(backend)
    }
}
