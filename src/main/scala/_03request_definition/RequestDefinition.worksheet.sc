import sttp.client3._

// ----- Request Definition Basics

val request = basicRequest
  .cookie("login", "me")
  .body("This is a test")
  .post(uri"http://endpoint.com/secret")

// val backend                                              = HttpClientSyncBackend()
// val response: Identity[Response[Either[String, String]]] = request.send(backend)

basicRequest.get(uri"http://httpbin.org/ip").toCurl

// ----- Headers

basicRequest.header("User-Agent", "myapp")

import sttp.model._

basicRequest.header(Header("k1", "v1"), replaceExisting = false)
basicRequest.header("k2", "v2")
basicRequest.header("k3", "v3", replaceExisting = true)
basicRequest.headers(Map("k4" -> "v4", "k5" -> "v5"))
basicRequest.headers(Header("k9", "v9"), Header("k10", "v10"), Header("k11", "v11"))

basicRequest.contentType("application/json")
basicRequest.contentType("application/json", "iso-8859-1")
basicRequest.contentLength(128)
basicRequest.acceptEncoding("gzip, deflate")

// ----- Cookies

import sttp.model.headers.CookieWithMeta

basicRequest
  .cookie("k1", "v1")
  .cookie("k2" -> "v2")
  .cookies("k3" -> "v3", "k4" -> "v4")
  .cookies(Seq(CookieWithMeta("k5", "k5"), CookieWithMeta("k6", "k6")))

val backend = HttpClientSyncBackend()

val loginRequest01 = basicRequest
  .cookie("login", "me")
  .body("This is a test")
  .post(uri"http://httpbin.org/post")
val response01     = loginRequest01.send(backend)

basicRequest.cookies(response01)

val loginRequest02        = basicRequest
  .cookie("login", "me")
  .body("This is a test")
  .post(uri"http://endpoint.com")
val response02            = loginRequest02.send(backend)
val cookiesFromResponse02 = response02.unsafeCookies

basicRequest.cookies(cookiesFromResponse02)

// ----- Authentication

val username = "mary"
val password = "p@assword"
basicRequest.auth.basic(username, password)

val token = "zMDjRfl76ZC9Ub0wnz4XsNiRVBChTYbJcE3F"
basicRequest.auth.bearer(token)

// Digest authentication
val myBackend: SttpBackend[Identity, Any] = HttpClientSyncBackend()
val digestBackend                         = new DigestAuthenticationBackend(myBackend)

val secureRequest = basicRequest.auth.digest(username, password)

val secureProxyRequest = basicRequest.proxyAuth.digest(username, password)

// OAuth2
import sttp.client3.circe._
import io.circe._
import io.circe.generic.semiauto._

val authCode                                                = "SplxlOBeZQQYbYS6WxSbIA"
val clientId                                                = "myClient123"
val clientSecret                                            = "s3cret"
case class MyTokenResponse(access_token: String, scope: String, token_type: String, refresh_token: Option[String])
implicit val tokenResponseDecoder: Decoder[MyTokenResponse] = deriveDecoder[MyTokenResponse]
// val backend                                                 = HttpClientSyncBackend()

val tokenRequest = basicRequest
  .post(uri"https://github.com/login/oauth/access_token?code=$authCode&grant_type=authorization_code")
  .auth
  .basic(clientId, clientSecret)
  .header("accept", "application/json")
val authResponse = tokenRequest.response(asJson[MyTokenResponse]).send(HttpClientSyncBackend())
val accessToken  = authResponse.body.map(_.access_token)

// ----- Body

// Text data
basicRequest.body("Hello, world!")
basicRequest.body("Hello, world!", "utf-8")

// Binary data
val bytes: Array[Byte] = "Hello, world!".getBytes("utf-8")
basicRequest.body(bytes)

import java.nio.ByteBuffer
val byteBuffer: ByteBuffer = ByteBuffer.wrap(bytes)
basicRequest.body(byteBuffer)

import java.io.ByteArrayInputStream
val inputStream: ByteArrayInputStream = new ByteArrayInputStream(bytes)
basicRequest.body(inputStream)

// Uploading files
// To upload a file, simply set the request body as a File or Path:
import java.io.File
basicRequest.body(new File("README.md"))

import java.nio.file.Path
basicRequest.body(Path.of("README.md"))

// Form data
basicRequest.body(Map("k1" -> "v1"))
basicRequest.body(Map("k1" -> "v1"), "utf-8")
basicRequest.body("k1" -> "v1", "k2" -> "v2")
basicRequest.body(Seq("k1" -> "v1", "k2" -> "v2"), "utf-8")

// Body serializers
import sttp.model.MediaType

case class Person(name: String, surname: String, age: Int)

// for this example, assuming names/surnames can't contain commas
implicit val personSerializer: BodySerializer[Person] = { p: Person =>
  val serialized = s"${p.name},${p.surname},${p.age}"
  StringBody(serialized, "UTF-8", MediaType.TextCsv)
}

basicRequest.body(Person("mary", "smith", 67))

// ----- Multipart requests

basicRequest.multipartBody(Seq(multipart("p1", "v1"), multipart("p2", "v2")))
basicRequest.multipartBody(multipart("p1", "v1"), multipart("p2", "v2"))

val someFile = new File("/sample/path")

basicRequest.multipartBody(
  multipart("text_part", "data1"),
  multipartFile("file_part", someFile), // someFile: File
  multipart("form_part", Map("x" -> "10", "y" -> "yes"))
)

val logoFile = new File("/sample/path/logo123.jpg")
val docFile  = new File("/sample/path/doc123.doc")
basicRequest.multipartBody(
  multipartFile("logo", logoFile).fileName("logo.jpg").contentType("image/jpg"),
  multipartFile("text", docFile).fileName("text.doc")
)

// ----- Streaming

// Akka streams

import akka.stream.scaladsl.Source
import akka.util.ByteString

val chunks                          = "Streaming test".getBytes("utf-8").grouped(10).to(Iterable)
val source: Source[ByteString, Any] = Source.apply(chunks.toList.map(ByteString(_)))

basicRequest
  .streamBody(sttp.capabilities.akka.AkkaStreams)(source)
  .post(uri"...")

// fs2 streams

import fs2._
import cats.effect.IO

val fs2Stream = fs2.Stream.emits("Streaming test".getBytes("utf-8")).covary[IO]

// basicRequest
//   .streamBody(new sttp.capabilities.Streams[IO])(fs2Stream)
//   .post(uri"...")
