package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{WorkspaceService => LspWorkspaceService}
import java.util.concurrent.CompletableFuture
import com.typesafe.scalalogging._

/** Handles document workspace configuration and updates.
 *  https://microsoft.github.io/language-server-protocol/specifications/specification-current/#workspace
 */
class WorkspaceService extends LspWorkspaceService with LazyLogging {
  /** Sent by client when configuration options are changed. We may make use
   *  of this in the future, but for now we don't need any configuration
   *  options.
   */
  override def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {
    logger.info(s"Changed configuration: ${params.getSettings}")
  }

  /** Sent by client when there are changes to the files watched by the client.
   *  This is irrelevant for our web-based reasoner interface.
   */
  override def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {
    logger.info(s"Changed watched files: ${params.getChanges}")
  }
}
