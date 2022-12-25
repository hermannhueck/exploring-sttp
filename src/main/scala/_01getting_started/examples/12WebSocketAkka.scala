package _01getting_started.examples

import scala.util.chaining._
import util._

import sttp.client3._
import sttp.client3.akkahttp.AkkaHttpBackend
import sttp.ws.WebSocket

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import sttp.capabilities.akka.AkkaStreams
import sttp.capabilities.WebSockets

object WebSocketAkka extends App {

  line80.green pipe println

  def useWebSocket(ws: WebSocket[Future]): Future[Unit] = {
    def send(i: Int) = ws.sendText(s"Hello $i!")
    def receive()    = ws.receiveText().map(t => println(s"RECEIVED: $t"))
    for {
      _ <- send(1)
      _ <- send(2)
      _ <- receive()
      _ <- receive()
    } yield ()
  }

  val backend: SttpBackend[Future, AkkaStreams with WebSockets] =
    AkkaHttpBackend()

  basicRequest
    .response(asWebSocket(useWebSocket))
    .get(uri"wss://ws.postman-echo.com/raw")
    .send(backend)
    .onComplete { _ =>
      backend.close()
      line80.green pipe println
    }
}
