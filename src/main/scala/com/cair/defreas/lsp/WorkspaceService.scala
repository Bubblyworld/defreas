package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{WorkspaceService => LspWorkspaceService}
import java.util.concurrent.CompletableFuture

/** Handles document workspace configuration and updates.
 *  https://microsoft.github.io/language-server-protocol/specifications/specification-current/#workspace
 */
class WorkspaceService extends LspWorkspaceService {
  /** Sent by client when configuration options are changed. We may make use
   *  of this in the future, but for now we don't need any configuration
   *  options.
   */
  def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {
    println(s"Changed configuration: ${params.settings}")
  }

  /** Sent by client when there are changes to the files watched by the client.
   *  This is irrelevant for our web-based reasoner interface.
   */
  def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {
    println(s"Changed watched files: ${params.changes}")
  }
}