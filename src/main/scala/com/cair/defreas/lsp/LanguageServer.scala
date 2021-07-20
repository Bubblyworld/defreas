package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.launch._
import org.eclipse.lsp4j.services.{LanguageServer => LspLanguageServer}
import cats.effect.implicits._
import cats.effect.{ContextShift, ExitCase, Sync, Timer}
import cats.syntax.all._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.CompletableFuture
import com.typesafe.scalalogging._
import scala.jdk.CollectionConverters._

// https://github.com/eclipse/lsp4j/blob/master/org.eclipse.lsp4j/src/main/java/org/eclipse/lsp4j/services/LanguageServer.java
//
// There are some issues with the way scala 12 handles code generation with
// annotations, see these relevant issues:
//    1) https://github.com/eclipse/lsp4j/issues/556 (override all methods)
//    2) https://github.com/eclipse/lsp4j/issues/322 (expicit annotations)
//    3) https://github.com/eclipse/lsp4j/issues/313 (no extends)
//    4) https://github.com/eclipse/lsp4j/issues/127 (sbt workaround!)
class LanguageServer extends LspLanguageServer with LazyLogging {
  val workspaceService = new WorkspaceService()
  val textDocumentService = new TextDocumentService()

  def run() =
    LSPLauncher
      .createServerLauncher(this, System.in, System.out)
      .startListening()

  override def initialize(params: InitializeParams): CompletableFuture[InitializeResult] = {
    logger.info("LanguageServer/initialize");

    val serverCapabilities = new ServerCapabilities()
    serverCapabilities.setHoverProvider(true)
    serverCapabilities.setCompletionProvider(
      new CompletionOptions(true, List("a", "b", "c").asJava))

    return CompletableFuture.completedFuture(
      new InitializeResult(serverCapabilities))
  }

  override def getTextDocumentService(): TextDocumentService = {
    logger.info("LanguageServer/getTextDocumentService");

    return textDocumentService
  }

  override def getWorkspaceService(): WorkspaceService = {
    logger.info("LanguageServer/getWorkspaceService");

    return workspaceService
  }

  override def exit(): Unit = {
    logger.info("LanguageServer/exit");
  }

  override def shutdown(): CompletableFuture[Object] = {
    logger.info("LanguageServer/shutdown");
    return CompletableFuture.completedFuture[Object](().asInstanceOf[Object])
  }
}

/** A utility object for passing from Java futures to Scala IO effects.
 *  https://gist.github.com/Daenyth/47a19a8f632e00a136f8647f3d9b5994 */
 // TODO understand this, then move somewhere useful
object JavaFuture {
  type JFuture[A] = java.util.concurrent.Future[A]

  def toIO[F[_], A](fa: F[JFuture[A]], pollInterval: FiniteDuration)(
      implicit F: Sync[F], timer: Timer[F]): F[A] = {

    def loop(jf: JFuture[A]): F[A] =
      F.delay(jf.isDone).flatMap { isDone =>
        if (isDone) F.delay(jf.get)
        else timer.sleep(pollInterval) *> loop(jf)
      }

    fa.flatMap { jf =>
      loop(jf)
        .guaranteeCase {
          case ExitCase.Canceled =>
            F.delay(jf.cancel(true)).void
          case _ => F.unit
        }
    }
  }

  def toIO[F[_], A](fa: F[JFuture[A]], ctx: ExecutionContext)(
      implicit F: Sync[F], CS: ContextShift[F]): F[A] =
    fa.flatMap { jf => CS.evalOn(ctx)(F.delay(jf.get)) }
}
