package com.cair.defreas.server

import cats.effect._
import org.http4s._
import org.http4s.server.staticcontent._

/** Defines the static file serving route for the HTTP server. */
object StaticRoutes {
  /** Route directory of static content. */
  val rootDir = "./web/dist"

  def apply(blocker: Blocker)(implicit cs: ContextShift[IO]): List[HttpRoutes[IO]] =
    List(fileService[IO](FileService.Config(rootDir, blocker)))
}
