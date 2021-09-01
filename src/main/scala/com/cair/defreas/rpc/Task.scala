package com.cair.defreas.rpc

import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._
import cats.effect._

case class Task[A, B](
  id: String,
  description: String,
  fn: A => B,
  valueA: Value[A],
  valueB: Value[B]
) {
  def httpInputSchema() =
    Ok(valueA.schema)

  def httpOutputSchema() =
    Ok(valueB.schema)

  def httpDescription() =
    Ok(getJsonDescription()) 

  def httpRun(req: Request[IO], reg: Registry) = {
    implicit val jsonDecoder: Decoder[A] = valueA.codec(reg) 
    implicit val entityDecoder = jsonOf[IO, A]

    for {
      input <- req.as[A]
      res <- Ok(valueB.codec(reg).apply(fn(input)))
    } yield res
  }

  def getJsonDescription() =
    TaskDescription(
      id,
      description,
      valueA.schema,
      valueB.schema
    ).asJson

  // For type erasure when storing inhomogenous tasks.
  trait Handler {
    def handle[A, B](task: Task[A, B]): Unit
  }

  def unwrap(handler: Handler) =
    handler.handle(this)
}

/** Human-readable specification of a task. */
case class TaskDescription(
  id: String,
  description: String,
  inputSchema: Json,
  outputSchema: Json,
)

sealed trait TaskError

case class TaskExecutionError(msg: String) extends TaskError
case class TaskSerialisationError(msg: String) extends TaskError
