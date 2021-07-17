package com.cair.defreas
package lsp

import org.eclipse.lsp4j._
import org.eclipse.lsp4j.services._
import org.eclipse.lsp4j.services.{TextDocumentService => LspTextDocumentService}
import java.util.concurrent.CompletableFuture

/** Handles updates to text documents.
 *  https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textSynchronization
 */
//class TextDocumentService extends LspTextDocumentService {
//  def didChange(params: DidChangeTextDocumentParams): Unit = {
//    println(s"Changed document: ${params.textDocument.uri}")
//    println(s"         changes: ${params.contentChanges}")
//  }
//
//  def didClose(params: DidCloseTextDocumentParams): Unit = {
//    println(s"Closed document: ${params.textDocument.uri}")
//    println(s"       contents: ${params.textDocument.text}")
//  }
//
//  def didOpen(params: DidOpenTextDocumentParams): Unit = {
//    println(s"Opened document: ${params.textDocument.uri}")
//    println(s"       contents: ${params.textDocument.text}")
//  }
//
//  def didSave(params: DidSaveTextDocumentParams): Unit = {
//    println(s"Saved document: ${params.textDocument.uri}")
//  }
//}
