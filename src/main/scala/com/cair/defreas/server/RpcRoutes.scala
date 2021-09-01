package com.cair.defreas.server

import io.circe._
import io.circe.syntax._
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._

import com.cair.defreas.rpc._

object RpcRoutes {
  val task = new Task(
    "boolean_and",
    "Returns the logical conjunction of the inputs.",
    (value: (Boolean, Boolean)) => value._1 && value._2,
    Value.pair(Value.boolean, Value.boolean),
    Value.boolean
  )

  val reg = Registry.empty

  def apply(): List[HttpRoutes[IO]] = 
    List(
      inputSchemaRoute(),
      outputSchemaRoute(),
      runRoute(),
    )

  def inputSchemaRoute() =
    HttpRoutes.of[IO] {
      case GET -> Root / "test" / "input" => 
        task.httpInputSchema()
    }

  def outputSchemaRoute() =
    HttpRoutes.of[IO] {
      case GET -> Root / "test" / "output" => 
        task.httpOutputSchema()
    }

  def runRoute(): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case req @ POST -> Root / "test" =>
        task.httpRun(req, reg)
    }
}
