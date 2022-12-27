import sttp.client3._
import sttp.model._
import sttp.client3.testing._
import java.io.File
import scala.concurrent.duration._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

case class User(id: String)

// ----- Specifying behavior --------------------------------------------------

val testingBackend01 = SttpBackendStub
  .synchronous
  .whenRequestMatches(_.uri.path.startsWith(List("a", "b")))
  .thenRespond("Hello there!")
  .whenRequestMatches(_.method == Method.POST)
  .thenRespondServerError()

val response0101 = basicRequest.get(uri"http://example.org/a/b/c").send(testingBackend01)
response0101.code
response0101.body

val response0102 = basicRequest.post(uri"http://example.org/d/e").send(testingBackend01)
response0102.code
response0102.body

val testingBackend02 = SttpBackendStub
  .synchronous
  .whenRequestMatchesPartial({
    case r if r.uri.path.endsWith(List("partial10")) =>
      Response("Not found", StatusCode.NotFound)

    case r if r.uri.path.endsWith(List("partialAda")) =>
      // additional verification of the request is possible
      assert(r.body == StringBody("z", "utf-8"))
      Response.ok("Ada")
  })

val response0201 = basicRequest.get(uri"http://example.org/partial10").send(testingBackend02)
response0201.code
response0201.body

// val response0202 = basicRequest.post(uri"http://example.org/partialAda").send(testingBackend02)
// java.lang.AssertionError: assertion failed

val testingBackend03 = SttpBackendStub
  .asynchronousFuture
  .whenAnyRequest
  .thenRespondF(Future {
    Thread.sleep(2000)
    Response.ok(Right("OK"))
  })

val responseFuture03 = basicRequest.get(uri"http://example.org").send(testingBackend03)
val result03         = Await.result(responseFuture03, 3.seconds)
result03.code
result03.body

val testingBackend04 = SttpBackendStub
  .synchronous
  .whenAnyRequest
  .thenRespondF(req => Response.ok(Right(s"OK, got request sent to ${req.uri.host}")))

val response04 = basicRequest.get(uri"http://example.org").send(testingBackend04)
response04.code
response04.body

val testingBackend05: SttpBackendStub[Identity, Any] = SttpBackendStub
  .synchronous
  .whenAnyRequest
  .thenRespondCyclic("first", "second", "third")

basicRequest.get(uri"http://example.org").send(testingBackend05).body
basicRequest.get(uri"http://example.org").send(testingBackend05).body
basicRequest.get(uri"http://example.org").send(testingBackend05).body
basicRequest.get(uri"http://example.org").send(testingBackend05).body

val testingBackend06: SttpBackendStub[Identity, Any] = SttpBackendStub
  .synchronous
  .whenAnyRequest
  .thenRespondCyclicResponses(
    Response.ok[String]("first"),
    Response("error", StatusCode.InternalServerError, "Something went wrong")
  )

basicRequest.get(uri"http://example.org").send(testingBackend06).code
basicRequest.get(uri"http://example.org").send(testingBackend06).code
basicRequest.get(uri"http://example.org").send(testingBackend06).code
basicRequest.get(uri"http://example.org").send(testingBackend06).code
basicRequest.get(uri"http://example.org").send(testingBackend06).code

val testingBackend07 = SttpBackendStub
  .synchronous
  .whenRequestMatches(_.forceBodyAsString.contains("Hello, world!"))
  .thenRespond("Hello back!")

// basicRequest.get(uri"http://example.org").body("Hello World").send(testingBackend07)

// ----- Simulating exceptions --------------------------------------------------

// val testingBackend08 = SttpBackendStub
//   .synchronous
//   .whenRequestMatches(_ => true)
//   .thenRespond(
//     throw new SttpClientException.ConnectException(basicRequest.get(uri"http://example.com"), new RuntimeException)
//   )

// basicRequest.get(uri"http://example.org").send(testingBackend08)

// ----- Adjusting the response body type --------------------------------------------------

// ----- Example: returning JSON --------------------------------------------------

val testingBackend09 = SttpBackendStub
  .synchronous
  .whenRequestMatches(_ => true)
  .thenRespond(""" {"username": "john", "age": 65 } """)

@annotation.nowarn("cat=unused")
def parseUserJson(a: Array[Byte]): User =
  User("john")

val response09 = basicRequest
  .get(uri"http://example.com")
  .response(asByteArrayAlways.map(parseUserJson))
  .send(testingBackend09)

response09.code
response09.body

// ----- Example: returning a file --------------------------------------------------

val destination = new File("path/to/file.ext")
basicRequest.get(uri"http://example.com").response(asFile(destination))

val fileResponseHandle = new File("path/to/file.ext")
SttpBackendStub
  .synchronous
  .whenRequestMatches(_ => true)
  .thenRespond(fileResponseHandle)

// import org.apache.commons.io.FileUtils
// import cats.effect._
// import sttp.client3.impl.cats.implicits._
// import sttp.monad.MonadAsyncError

// val sourceFile      = new File("path/to/file.ext")
// val destinationFile = new File("path/to/file.ext")
// SttpBackendStub(implicitly[MonadAsyncError[IO]])
//   .whenRequestMatches(_ => true)
//   .thenRespondF { _ =>
//     FileUtils.copyFile(sourceFile, destinationFile)
//     IO(Response(Right(destinationFile), StatusCode.Ok, ""))
//   }

// ----- Delegating to another backend --------------------------------------------------

val testingBackend10 =
  SttpBackendStub
    .withFallback(HttpClientSyncBackend())
    .whenRequestMatches(_.uri.path.startsWith(List("a")))
    .thenRespond("I'm a STUB!")

val response1001 = basicRequest.get(uri"http://api.internal/a").send(testingBackend10)
response1001.code
response1001.body

// val response1002 = basicRequest.post(uri"http://api.internal/b").send(testingBackend10)
// response1002.code
// response1002.body

// ----- Testing streams --------------------------------------------------

// ----- Testing web sockets --------------------------------------------------

// ----- WebSocketStub --------------------------------------------------

import sttp.ws.testing.WebSocketStub
import sttp.ws.WebSocketFrame

val webSocketStub = WebSocketStub
  .initialReceive(
    List(WebSocketFrame.text("Hello from the server!"))
  )
  .thenRespondS(0) {
    case (counter, tf: WebSocketFrame.Text) => (counter + 1, List(WebSocketFrame.text(s"echo: ${tf.payload}")))
    case (counter, _)                       => (counter, List.empty)
  }

val backend11 = SttpBackendStub.synchronous
backend11
  .whenAnyRequest
  .thenRespond(webSocketStub)

// ----- Verifying, that a request was sent --------------------------------------------------

import scala.util.Try

val testingBackend12 = new RecordingSttpBackend(
  SttpBackendStub
    .synchronous
    .whenRequestMatches(_.uri.path.startsWith(List("a", "b")))
    .thenRespond("Hello there!")
)

val response12 = basicRequest.get(uri"http://example.org/a/b/c").send(testingBackend12)
response12.code
response12.body

testingBackend12.allInteractions: List[(Request[_, _], Try[Response[_]])]
