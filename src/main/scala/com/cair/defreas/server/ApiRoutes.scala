package com.cair.defreas.server

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
    pkgs.map(packageRoutes) :+ versionRoute()

  def versionRoute(): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "version" => 
        Ok(appVersion)
    }

  case class TestRequest(id: String, rand: Option[Int])
  case class TestResponse(res: String)
  implicit val testRequestDecoder = jsonOf[IO, TestRequest]

  def packageRoutes(pkg: Package): HttpRoutes[IO] = {
    var res = List[HttpRoutes[IO]]()
    pkg.unwrapTasks(new PackageHandler {
      def handle[L : Logic, A, B](task: Task[L, A, B]): Unit =
        res = res :+ taskRoute[L, A, B](pkg.id(), task)
    })

    if (res.length == 0) {
      return HttpRoutes.empty[IO]
    } else {
      return res.reduce(_.combineK(_))
    }
  }

  def taskRoute[L : Logic, A, B](packageID: String, task: Task[L, A, B]): HttpRoutes[IO] = {
    val logicID = implicitly[Logic[L]].id 
    val taskID = task.id()

    println(s"Defining route: http://localhost:8080/tasks/${packageID}/${logicID}/${taskID}")
    return HttpRoutes.of[IO] {
      case jsonReq @ POST -> Root / "tasks" / packageID / logicID / taskID =>
        for {
          // TODO
          req <- jsonReq.as[TestRequest]
          res <- Ok(TestResponse(req.id + req.rand.toString()).asJson)
        } yield res
    }
  }
}
