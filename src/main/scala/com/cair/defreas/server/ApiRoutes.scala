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
        res = res :+ taskRoute[L, A, B](pkg, task)
    })

    if (res.length == 0) {
      return HttpRoutes.empty[IO]
    } else {
      return res.reduce(_.combineK(_))
    }
  }

  case class TaskRequest[L : Logic](syntaxID: String, context: WrappedTaskContext)
  case class TaskResponse[L : Logic](context: Option[WrappedTaskContext], err: Option[String])
  implicit def taskDecoder[L : Logic]: EntityDecoder[IO, TaskRequest[L]] = 
    jsonOf[IO, TaskRequest[L]]

  def taskRoute[L : Logic, A, B](pkg: Package, task: Task[L, A, B]): HttpRoutes[IO] = {
    val taskID = task.id()
    val logicID = implicitly[Logic[L]].id 
    val packageID = pkg.id()
    println(s"Defining route: http://localhost:8080/tasks/${packageID}/${logicID}/${taskID}")

    def handle(req: TaskRequest[L]): TaskResponse[L] = {
      pkg.getSyntax(req.syntaxID) match {
        case None => TaskResponse(None, Option(s"no syntax with id '${req.syntaxID}' found"))
        case Some(syntax) => {
          req.context.parseWith(syntax) match {
            case Left(err) => TaskResponse(None, Option(err.toString()))
            case Right(ctx) => TaskResponse(
              Option(WrappedTaskContext(task.run(ctx))),
              None
            )
          }
        }
      }
    }

    return HttpRoutes.of[IO] {
      case jsonReq @ POST -> Root / "tasks" / packageID / logicID / taskID =>
        for {
          req <- jsonReq.as[TaskRequest[L]]
          res <- Ok(handle(req).asJson)
        } yield res
    }
  }
  
  /** We don't know which syntax to use to parse the given input formulas yet,
   *  so we keep formulas/knowledge bases as strings at the boundaries. */
  case class WrappedTaskContext(
    bool: Option[Boolean],
    formula: Option[String],
    knowledgeBase: Option[List[String]],
  ) {
    /** Converts a WrappedTaskContext to a Context using a Syntax instance. */
    def parseWith[L : Logic](syntax: Syntax[L]): Either[ParserError, TaskContext[L]] =
      parseFormula(syntax).flatMap(f => {
        parseKnowledgeBase(syntax).map(kb => {
          TaskContext[L](bool, f, kb)
        })
      })

    private def parseFormula[L : Logic](syntax: Syntax[L]): Either[ParserError, Option[L]] =
      formula match {
        case None => Right(None)
        case Some(f) => syntax.parse(f) match {
          case Left(err) => Left(err)
          case Right(l) => Right(Some(l))
        }
      }

    private def parseKnowledgeBase[L : Logic](syntax: Syntax[L]): Either[ParserError, Option[List[L]]] =
      knowledgeBase match {
        case None => Right(None)
        case Some(kb) => eitherMap(kb, syntax.parse) match {
          case Left(err) => Left(err)
          case Right(ls) => Right(Some(ls))
        }
      }

    private def eitherMap[A, B, C](as: List[A], f: A => Either[B, C]): Either[B, List[C]] = {
      var res = List.empty[C]
      as.map(f(_) match {
          case Left(b) => return Left(b)
          case Right(c) => res = res :+ c
        }
      )

      return Right(res)
    }
  }

  object WrappedTaskContext {
    def apply[L : Logic](ctx: TaskContext[L]): WrappedTaskContext =
      WrappedTaskContext(
        ctx.bool,
        ctx.formula.map(_.toString),
        ctx.knowledgeBase.map(_.map(_.toString)), // TODO
      )
  }
}
