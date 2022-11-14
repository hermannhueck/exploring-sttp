// Responses

import sttp.model._
import sttp.client3._

// ----- Responses

val backend  = HttpClientSyncBackend()
val request  = basicRequest
  .get(uri"https://httpbin.org/get")
val response = request.send(backend)

val singleHeader: Option[String] = response.header(HeaderNames.Server)
val multipleHeaders: Seq[String] = response.headers(HeaderNames.Allow)

val contentType: Option[String] = response.contentType
val contentLength: Option[Long] = response.contentLength

import sttp.model.headers.CookieWithMeta

val cookies: Seq[CookieWithMeta] = response.unsafeCookies

// ----- Response body specification

// -- Basic response specifications

basicRequest

basicRequest.response(asByteArray)

basicRequest.response(ignore)

import java.io._

val someFile = new File("some/path")
// saves the response body to a file
basicRequest.response(asFile(someFile))

// -- Failing when the response code is not 2xx

basicRequest.response(asString.getRight): PartialRequest[String, Any]

// -- Custom body deserializers

val asInt: ResponseAs[Either[String, Int], Any] = asString.mapRight(_.toInt)

basicRequest
  .get(uri"http://example.com")
  .response(asInt)

object JsonExample {

  type JsonError
  type JsonAST

  def parseJson(json: String): Either[JsonError, JsonAST] = ???
  val asJson: ResponseAs[Either[JsonError, JsonAST], Any] = asStringAlways.map(parseJson)

  basicRequest.response(asJson)
}

// -- Response-metadata dependent deserializers

import sttp.model._
import sttp.client3.circe._
import io.circe._
import io.circe.generic.auto._

sealed trait MyModel
case class SuccessModel(name: String, age: Int) extends MyModel
case class ErrorModel(message: String)          extends MyModel

val myRequest: Request[Either[ResponseException[String, io.circe.Error], MyModel], Nothing] =
  basicRequest
    .get(uri"https://example.com")
    .response(
      fromMetadata(
        asJson[ErrorModel],
        ConditionalResponseAs(_.code == StatusCode.Ok, asJson[SuccessModel])
      )
    )

case class MyModel2(p1: Int)
sealed trait MyErrorModel
case class Conflict(message: String)     extends MyErrorModel
case class BadRequest(message: String)   extends MyErrorModel
case class GenericError(message: String) extends MyErrorModel

val myRequest2: Request[Either[ResponseException[MyErrorModel, io.circe.Error], MyModel2], Nothing] =
  basicRequest
    .get(uri"https://example.com")
    .response(asJsonEither[MyErrorModel, MyModel2])

// -- Streaming

object StreamingExample {

  import sttp.capabilities.{Effect, Streams}
  import sttp.model.ResponseMetadata

  def asStream[F[_], T, S](s: Streams[S])(f: s.BinaryStream => F[T]): ResponseAs[Either[String, T], Effect[F] with S] =
    ???

  def asStreamWithMetadata[F[_], T, S](s: Streams[S])(
      f: (s.BinaryStream, ResponseMetadata) => F[T]
  ): ResponseAs[Either[String, T], Effect[F] with S] =
    ???

  def asStreamAlways[F[_], T, S](s: Streams[S])(f: s.BinaryStream => F[T]): ResponseAs[T, Effect[F] with S] =
    ???

  def asStreamAlwaysWithMetadata[F[_], T, S](s: Streams[S])(
      f: (s.BinaryStream, ResponseMetadata) => F[T]
  ): ResponseAs[T, Effect[F] with S] =
    ???

  def asStreamUnsafe[S](s: Streams[S]): ResponseAs[Either[String, s.BinaryStream], S] =
    ???

  def asStreamUnsafeAlways[S](s: Streams[S]): ResponseAs[s.BinaryStream, S] =
    ???
}

import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import sttp.capabilities.akka.AkkaStreams
import sttp.client3.akkahttp.AkkaHttpBackend

val backend2: SttpBackend[Future, AkkaStreams] = AkkaHttpBackend()

val futureResponse: Future[Response[Either[String, Source[ByteString, Any]]]] =
  basicRequest
    .post(uri"http://httpbin.org/post")
    .response(asStreamUnsafe(AkkaStreams))
    .send(backend2)

val result = Await.result(futureResponse.map(_.body), 3.seconds)

import akka.actor.ActorSystem
implicit val system: ActorSystem = ActorSystem()

result match {
  case Right(source) =>
    source.runForeach(bs => println(bs.utf8String)).onComplete(_ => system.terminate())
  case Left(error)   =>
    println(error)
}
