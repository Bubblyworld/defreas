package com.cair.defreas

import cats.effect._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.implicits._
import scala.concurrent.ExecutionContext.global

case class ParserRequest(input: String)
case class ParserResponse(error: Boolean)

object Main extends IOApp {
  val appVersion = "DefReas 0.0.1"

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "version" =>
      Ok(appVersion)

    case req @ POST -> Root / "parse" =>
      for {
        req <- req.as(implicitly, jsonOf[IO, ParserRequest])
        res <- Ok(ParserResponse(true).asJson)
      } yield (res)
    
  }.orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
