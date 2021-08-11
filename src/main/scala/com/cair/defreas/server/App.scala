package com.cair.defreas.server

import cats.effect._
import cats.implicits._
import org.http4s._

import com.cair.defreas.types._

/** The combination of all API and static routes in a single app. */
object App {
  def apply(
    blocker: Blocker,
    packages: List[Package],
    syntaxes: DependentMap[String, Syntax]
  )(implicit cs: ContextShift[IO]): HttpRoutes[IO] =
    List(
      ApiRoutes(packages, syntaxes),
      StaticRoutes(blocker),
    ).flatten.reduce({ _.combineK(_) })
}
