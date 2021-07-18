package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{TextDocumentService => LspTextDocumentService}
import java.util.concurrent.CompletableFuture

/** Handles updates to text documents.
 *  https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textSynchronization
 */
class TextDocumentService extends LspTextDocumentService {
  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    println(s"Changed document: ${params.getTextDocument.getUri}")
    println(s"         changes: ${params.getContentChanges}")
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
    println(s"Closed document: ${params.getTextDocument.getUri}")
  }

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    println(s"Opened document: ${params.getTextDocument.getUri}")
    println(s"       contents: ${params.getTextDocument.getText}")
  }

  override def didSave(params: DidSaveTextDocumentParams): Unit = {
    println(s"Saved document: ${params.getTextDocument.getUri}")
  }
}
