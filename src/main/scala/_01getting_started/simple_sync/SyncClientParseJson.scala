package _01getting_started.simple_sync

import scala.util.chaining._
import util._

import sttp.model.Uri
import sttp.client3.{basicRequest, Request, Response, SimpleHttpClient, UriContext}

object SyncClientParseJson extends App {

  line80.green pipe println

  val uri: Uri                                      = uri"https://httpbin.org/get?hello=world"
  val request: Request[Either[String, String], Any] = basicRequest.get(uri)

  val client: SimpleHttpClient                   = SimpleHttpClient()
  val response: Response[Either[String, String]] = client.send(request) tap println

  s"$line10 response.code $line10".cyan pipe println
  response.code pipe println
  s"$line10 response.headers $line10".cyan pipe println
  response.headers pipe println
  s"$line10 response.body $line10".cyan pipe println
  response.body pipe println

  s"$line10 Final Result $line10".cyan pipe println
  response.body match {
    case Left(error) =>
      println(s"Got response ERROR:\n$error")
    case Right(body) =>
      println(s"BODY:\n${body}")
      s"$line10 Origin IP $line10".cyan pipe println
      // processBody1(body)
      // processBody2(body)
      // processBody3(body)
      processBody4(body)
  }

  def processBody1(jsonString: String): Unit = {
    case class Origin(origin: String)
    import io.circe.parser.decode
    implicit val originDecoder = io.circe.generic.semiauto.deriveDecoder[Origin]
    decode[Origin](jsonString) match {
      case Left(error)   =>
        println(s"Got JSON ERROR:\n$error")
      case Right(origin) =>
        println(s"Origin's ip: ${origin.origin}")
    }
  }

  def processBody2(jsonString: String): Unit = {
    import io.circe.parser.decode
    import io.circe.generic.JsonCodec
    @JsonCodec case class Origin(origin: String)
    decode[Origin](jsonString) match {
      case Left(error)   =>
        println(s"Got JSON ERROR:\n$error")
      case Right(origin) =>
        println(s"Origin's ip: ${origin.origin}")
    }
  }

  def processBody3(jsonString: String): Unit = {
    case class Origin(origin: String)
    import io.circe.parser._
    parse(jsonString).flatMap(_.hcursor.downField("origin").as[String]) match {
      case Left(error)   =>
        println(s"Got JSON ERROR:\n$error")
      case Right(origin) =>
        println(s"Origin's ip: $origin")
    }
  }

  def processBody4(jsonString: String): Unit = {
    case class Origin(origin: String)
    import io.circe.parser._
    parse(jsonString) match {
      case Left(error) =>
        println(s"Got JSON ERROR: $error")
      case Right(json) =>
        json.asObject.flatMap(_.toMap.get("origin")) match {
          case None         =>
            println(s"Origin's ip not found")
          case Some(origin) =>
            println(s"Origin's ip: ${origin}")
        }
    }
  }

  line80.green pipe println
}
