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

  def apply(pkgs: List[Package]): List[HttpRoutes[IO]] = 
    pkgs.flatMap(packageRoutes) :+ versionRoute()

  /** Route that returns the version of the tool. */
  def versionRoute(): HttpRoutes[IO] = {
    println("/version (GET)")

    HttpRoutes.of[IO] {
      case GET -> Root / "version" => 
        Ok(appVersion)
    }
  }

  /** Combined routes for the given package. */
  def packageRoutes(pkg: Package): List[HttpRoutes[IO]] = {
    println(s"/packages/${pkg.id}")

    return pkg
      .listTasks()
      .flatMap(taskRoutes(pkg, _))
  }

  /** Combined routes for the given task. */
  def taskRoutes(pkg: Package, taskID: String): List[HttpRoutes[IO]] =
    return pkg.getTask(taskID) match {
      case Left(notFound) => List.empty
      case Right(taskWrapper) =>
        taskWrapper.unwrap(
          new Task.Handler[List[HttpRoutes[IO]]] {
            def handle[A : Value, B : Value](task: Task[A, B]) =
              List (
                taskRequirementsRoute(pkg, task),
                taskExecutionRoute(pkg, task)
              )
          }
        )
    }

  /** Route that provides input requirements for a task. */
  def taskRequirementsRoute[A : Value, B : Value](
    pkg: Package,
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
    task: Task[A, B]
  ): HttpRoutes[IO] = {
    println(s"  /tasks/${task.id} (POST)")

    HttpRoutes.of[IO] {
      case req @ POST -> Root / "packages" / pkg.id / "tasks" / task.id =>
        for {
          env <- req.as[Environment]
          res <- Ok(task.run(env, pkg.syntaxes).asJson)
        } yield res
    }
  }
}
