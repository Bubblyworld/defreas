package com.cair.defreas.server

import cats.effect._
import cats.implicits._
import org.http4s._

object routes {
  def apply(blocker: Blocker)(implicit cs: ContextShift[IO]): HttpRoutes[IO] =
    List(
      ApiRoutes(),
      StaticRoutes(blocker),
    ).flatten.reduce({ _.combineK(_) })
}
