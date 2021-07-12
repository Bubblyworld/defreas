package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{TextDocumentService => LspTextDocumentService}
import java.util.concurrent.CompletableFuture

class TextDocumentService extends LspTextDocumentService {
  def didChange(params: DidChangeTextDocumentParams): Unit = {}
  def didClose(params: DidCloseTextDocumentParams): Unit = {}
  def didOpen(params: DidOpenTextDocumentParams): Unit = {}
  def didSave(params: DidSaveTextDocumentParams): Unit = {}
}
