package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{LanguageServer => LspLanguageServer}
import java.util.concurrent.CompletableFuture

// https://github.com/eclipse/lsp4j/blob/master/org.eclipse.lsp4j/src/main/java/org/eclipse/lsp4j/services/LanguageServer.java
class LanguageServer extends LspLanguageServer {
  def initialize(params: InitializeParams): CompletableFuture[InitializeResult] = {
    return ().asInstanceOf[CompletableFuture[InitializeResult]]
  }

  def getTextDocumentService(): TextDocumentService = {
    return ().asInstanceOf[TextDocumentService]
  }

  def getWorkspaceService(): WorkspaceService = {
    return ().asInstanceOf[WorkspaceService]
  }

  def exit(): Unit = {
    println("Exiting language server - bye!")
  }

  def shutdown(): CompletableFuture[Object] = {
    println("Shutting down language server...")

    val future: CompletableFuture[Object] = new CompletableFuture()
    future.complete(().asInstanceOf[Object])
    return future
  }
}
