package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{TextDocumentService => LspTextDocumentService}
import org.eclipse.lsp4j.jsonrpc.messages._
import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters._

/** Handles updates to text documents.
 *  https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textSynchronization
 */
class TextDocumentService extends LspTextDocumentService {
  type JList[A] = java.util.List[A]

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    //println(s"Changed document: ${params.getTextDocument.getUri}")
    //println(s"         changes: ${params.getContentChanges}")
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
    //println(s"Closed document: ${params.getTextDocument.getUri}")
  }

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    //println(s"Opened document: ${params.getTextDocument.getUri}")
    //println(s"       contents: ${params.getTextDocument.getText}")
  }

  override def didSave(params: DidSaveTextDocumentParams): Unit = {
    //println(s"Saved document: ${params.getTextDocument.getUri}")
  }

  override def hover(params: HoverParams): CompletableFuture[Hover] = {
    val future = new CompletableFuture[Hover]()
    val contents = new MarkupContent("plaintext", "Nothing to see yet...");
    future.complete(new Hover(contents))

    return future
  }

  type CompletionResult = Either[JList[CompletionItem], CompletionList]
  override def completion(params: CompletionParams): CompletableFuture[CompletionResult] = {
    val future = new CompletableFuture[CompletionResult]() 
    val ci = new CompletionItem("label")
    ci.setKind(CompletionItemKind.Text)
    ci.setDetail("detail")
    ci.setDocumentation("documentation")
    ci.setInsertText("insertText")
    ci.setInsertTextFormat(InsertTextFormat.PlainText)
    future.complete(Either.forLeft(List(ci).asJava))

    return future
  }
}
