package com.cair.defreas.server

import io.circe._
import io.circe.syntax._
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._

import com.cair.defreas.rpc._

case class SerialisedBoolean(value: Boolean)

object RpcRoutes {
  val serial = new Serial[SerialisedBoolean] {
    val id = "sb_std"

    def print(value: SerialisedBoolean) =
      s"${value.value}"

    def parse(str: String) =
      str match {
        case "true" => Some(SerialisedBoolean(true))
        case "false" => Some(SerialisedBoolean(false))
        case _ => None
      }
  }

  val bValue = Value.boolean
  val sbValue = Value.serial("serialised_boolean", serial)
  val fn = (value: (Boolean, SerialisedBoolean)) =>
    SerialisedBoolean(value._1 && value._2.value)

  val task = new Task(
    "boolean_and",
    "Returns the logical conjunction of the inputs.",
    fn,
    Value.pair(bValue, sbValue),
    sbValue
  )

  val reg = Registry
    .empty
    .addSerial(serial)
    .addTask(task)

  def apply(): List[HttpRoutes[IO]] = 
    List(
      descriptionRoute(),
      inputSchemaRoute(),
      outputSchemaRoute(),
      runRoute(),
    )

  def descriptionRoute() =
    HttpRoutes.of[IO] {
      case GET -> Root / "test" =>
        task.httpDescription()
    }

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
