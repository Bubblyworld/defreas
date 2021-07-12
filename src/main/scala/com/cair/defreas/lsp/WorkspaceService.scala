package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{WorkspaceService => LspWorkspaceService}
import java.util.concurrent.CompletableFuture

class WorkspaceService extends LspWorkspaceService {
  def didChangeConfiguration(params: DidChangeConfigurationParams): Unit = {
  }

  def didChangeWatchedFiles(params: DidChangeWatchedFilesParams): Unit = {
  }
}
