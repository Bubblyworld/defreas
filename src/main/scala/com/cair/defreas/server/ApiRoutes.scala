package com.cair.defreas.server

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.staticcontent._

/** Defines the REST API route for the HTTP server. */
object ApiRoutes {
  val appVersion = "DefReas 0.0.1"

  def apply(): List[HttpRoutes[IO]] = 
    List(versionRoute())

  def versionRoute(): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "version" => 
        Ok(appVersion)
    }
}

