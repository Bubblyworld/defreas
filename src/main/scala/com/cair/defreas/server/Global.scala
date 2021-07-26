package com.cair.defreas.server

import cats.effect._
import cats.implicits._
import org.http4s._

import com.cair.defreas.types._

object routes {
  def apply(blocker: Blocker, packages: List[Package])(implicit cs: ContextShift[IO]): HttpRoutes[IO] =
    List(
      ApiRoutes(packages),
      StaticRoutes(blocker),
    ).flatten.reduce({ _.combineK(_) })
}
