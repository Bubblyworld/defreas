package com.cair.defreas

import cats.effect._
import org.http4s.server._
import org.http4s.server.blaze._
import org.http4s.implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.global

import com.cair.defreas.lsp._
import com.cair.defreas.server.App
import com.cair.defreas.types._
import com.cair.defreas.stdlib.basic
import com.cair.defreas.stdlib.propositional

object Main extends IOApp {
  val app: Resource[IO, Server[IO]] = {
    val pkgs = getPackages()
    val syntaxes = getSyntaxes(pkgs)

    for {
      blocker <- Blocker[IO]
      server <- BlazeServerBuilder[IO](global)
        .bindHttp(8080, "localhost")
        .withHttpApp(App(blocker, pkgs, syntaxes).orNotFound)
        .resource
    } yield server
  }

  /** Runs the DefReas platform. Two commands are currently supported:
   *    1) 'web': serves the API and web platform on port 8080
   *    2) 'lsp': runs the language server on stdin/sdout */
  override def run(args: List[String]): IO[ExitCode] = {
    if (args.length < 1) {
      return IO {
        println("Error: please supply at least one command to defreas")
        ExitCode.Error
      }
    }

    if (args(0) == "web") {
      app.use(_ => IO.never).as(ExitCode.Success)
    } else if (args(0) == "lsp") {
      for {
        _ <- JavaFuture.toIO(IO(new LanguageServer().run()), 100.milliseconds)
      } yield ExitCode.Success
    } else IO {
      println("Error: supported commands are 'web' and 'lsp'")
      ExitCode.Error
    }
  }

  /** Returns every package that the tool can find. */
  def getPackages(): List[Package] =
    List(
      basic.getPackage(),
      propositional.getPackage()
    )

  /** Combines syntaxes from every package with namespacing. */
  def getSyntaxes(pkgs: List[Package]): DependentMap[String, Syntax] =
    pkgs.map(
      pkg => pkg.syntaxes.map(
        kv => (kv._1.map(
          id => s"${pkg.id}/${id}"
        ), kv._2)
      )
    ).reduce(_ ++ _) 
}
