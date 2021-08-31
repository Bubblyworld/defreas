package com.cair.defreas.types

/** A textual representation of values of A. */
trait Syntax[A] extends SyntaxPrinter[A] with SyntaxParser[A] with Syntax.Wrapper {
  /** Package-unique identifier of the syntax. */
  val id: String

  def unwrap(handler: Syntax.Handler): Unit =
    handler.handle(this) 
}

object Syntax {
  /** Existential type erasor for Syntax types. */
  sealed trait Wrapper {
    def unwrap(handler: Handler): Unit
  }

  /** Existential type handler for Syntax types. */
  trait Handler {
    def handle[A](syntax: Syntax[A]): Unit
  }
}

/** Something that can print values of A as strings. */
trait SyntaxPrinter[A] {
  def print(value: A): String
}

/** Something that can parse values of A from strings. */
trait SyntaxParser[A] {
  def parse(str: String): Either[ParseError, A]
}

sealed trait SyntaxError extends Error

/** An error that may occur during the parsing of a value. */
case class ParseError(msg: String) extends SyntaxError {
  override def toString() = msg
}
