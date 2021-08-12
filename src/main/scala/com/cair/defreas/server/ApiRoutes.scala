package com.cair.defreas.server

import cats.data._
import cats.effect._
import cats.implicits._
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

  def apply(
    pkgs: List[Package],
    syntaxes: DependentMap[String, Syntax]
  ): List[HttpRoutes[IO]] = 
    pkgs.flatMap(packageRoutes(_, syntaxes)) :+ versionRoute()

  /** Route that returns the version of the tool. */
  def versionRoute(): HttpRoutes[IO] = {
    println("/version (GET)")

    HttpRoutes.of[IO] {
      case GET -> Root / "version" => 
        Ok(appVersion)
    }
  }

  /** Combined routes for the given package. */
  def packageRoutes(
    pkg: Package,
    syntaxes: DependentMap[String, Syntax]
  ): List[HttpRoutes[IO]] = {
    println(s"/packages/${pkg.id}")

    return pkg
      .listTasks()
      .flatMap(taskRoutes(pkg, syntaxes, _))
  }

  /** Combined routes for the given task. */
  def taskRoutes(
    pkg: Package,
    syntaxes: DependentMap[String, Syntax],
    taskID: String
  ): List[HttpRoutes[IO]] =
    return pkg.getTask(taskID) match {
      case Left(notFound) => List.empty
      case Right(taskWrapper) =>
        taskWrapper.unwrap(
          new Task.Handler[List[HttpRoutes[IO]]] {
            def handle[A : Value, B : Value](task: Task[A, B]) =
              List (
                taskRequirementsRoute(pkg, syntaxes, task),
                taskExecutionRoute(pkg, syntaxes, task)
              )
          }
        )
    }

  /** Route that provides input requirements for a task. */
  def taskRequirementsRoute[A : Value, B : Value](
    pkg: Package,
    syntaxes: DependentMap[String, Syntax],
    task: Task[A, B]
  ): HttpRoutes[IO] = {
    println(s"  /tasks/${task.id} (GET)")

    HttpRoutes.of[IO] {
      case GET -> Root / "packages" / pkg.id / "tasks" / task.id =>
        Ok(implicitly[Value[A]].requirements().asJson)
    }
  }

  /** For decoding JSON environments in POST requests. */
  implicit val envDecoder: EntityDecoder[IO, Environment] =
    jsonOf[IO, Environment]

  /** Route that runs a task on given input. */
  def taskExecutionRoute[A : Value, B : Value](
    pkg: Package, 
    syntaxes: DependentMap[String, Syntax],
    task: Task[A, B]
  ): HttpRoutes[IO] = {
    println(s"  /tasks/${task.id} (POST)")

    HttpRoutes.of[IO] {
      case req @ POST -> Root / "packages" / pkg.id / "tasks" / task.id =>
        for {
          _ <- IO(println("here"))
          env <- req.as[Environment]
          _ <- IO(println(env))
          res <- Ok(task.run(env, syntaxes).asJson)
        } yield res
    }
  }
}
