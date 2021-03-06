package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{TextDocumentService => LspTextDocumentService}
import org.eclipse.lsp4j.jsonrpc.messages._
import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters._
import com.typesafe.scalalogging._

/** Handles updates to text documents.
 *  https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textSynchronization
 */
class TextDocumentService extends LspTextDocumentService with LazyLogging {
  type JList[A] = java.util.List[A]

  override def didChange(params: DidChangeTextDocumentParams): Unit = {
    logger.info(s"Changed document: ${params.getTextDocument.getUri}")
  }

  override def didClose(params: DidCloseTextDocumentParams): Unit = {
    logger.info(s"Closed document: ${params.getTextDocument.getUri}")
  }

  override def didOpen(params: DidOpenTextDocumentParams): Unit = {
    logger.info(s"Opened document: ${params.getTextDocument.getUri}")
  }

  override def didSave(params: DidSaveTextDocumentParams): Unit = {
    logger.info(s"Saved document: ${params.getTextDocument.getUri}")
  }

  override def hover(params: HoverParams): CompletableFuture[Hover] = {
    logger.info(s"TextDocumentService/hover: ${params.getPosition()}")

    val contents = new MarkupContent("plaintext", "Nothing to see yet...");
    return CompletableFuture.completedFuture(new Hover(contents))
  }

  type CompletionResult = Either[JList[CompletionItem], CompletionList]
  override def completion(params: CompletionParams): CompletableFuture[CompletionResult] = {
    logger.info(s"TextDocumentService/completion")

    val ci = new CompletionItem("label")
    ci.setKind(CompletionItemKind.Text)
    ci.setDetail("detail")
    ci.setDocumentation("documentation")
    ci.setInsertText("insertText")
    ci.setInsertTextFormat(InsertTextFormat.PlainText)

    return CompletableFuture.completedFuture(Either.forLeft(List(ci).asJava))
  }
}
