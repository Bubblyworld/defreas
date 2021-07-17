package com.cair.defreas

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server._
import org.http4s.server.blaze._
import org.http4s.server.staticcontent._
import org.http4s.implicits._
import scala.concurrent.ExecutionContext.global

case class ParserRequest(input: String)
case class ParserResponse(error: Boolean)

object Main extends IOApp {
  val appVersion = "DefReas 0.0.1"

  val apiService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "version" =>
      Ok(appVersion)

    case req @ POST -> Root / "parse" =>
      for {
        req <- req.as(implicitly, jsonOf[IO, ParserRequest])
        res <- Ok(ParserResponse(true).asJson)
      } yield (res)
  }

  def webServiceWithBlocker(blocker: Blocker): HttpRoutes[IO] =
    webService[IO](FileService.Config("./web", blocker))

  def appService(blocker: Blocker): HttpRoutes[IO] =
    webServiceWithBlocker(blocker)
      .combineK(apiService)
  
  val app: Resource[IO, Server[IO]] =
    for {
      blocker <- Blocker[IO]
      server <- BlazeServerBuilder[IO](global)
        .bindHttp(8080, "localhost")
        .withHttpApp(appService(blocker).orNotFound)
        .resource
    } yield server

  override def run(args: List[String]): IO[ExitCode] =
    app.use(_ => IO.never).as(ExitCode.Success)
}
