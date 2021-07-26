package com.cair.defreas.server

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import com.cair.defreas.types._

/** Defines the REST API route for the HTTP server. */
object ApiRoutes {
  val appVersion = "DefReas 0.0.1"

  def apply(packages: List[Package]): List[HttpRoutes[IO]] = 
    List(versionRoute(), testRoute())

  def versionRoute(): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "version" => 
        Ok(appVersion)
    }

  case class TestRequest(id: String, rand: Option[Int])
  case class TestResponse(res: String)
  implicit val testRequestDecoder = jsonOf[IO, TestRequest]

  def testRoute(): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case jsonReq @ POST -> Root / "test" =>
        for {
          req <- jsonReq.as[TestRequest]
          res <- Ok(TestResponse(req.id + req.rand.toString()).asJson)
        } yield res
    }
}
