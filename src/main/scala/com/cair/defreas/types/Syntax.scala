package com.cair.defreas.types

import scala.util.parsing.combinator._

/** Represents an input format, or syntax, for a particular logic. */
class Syntax[L : Logic](id: String, parser: Parser[L]) {
  def id(): String = id
  def parse(program: String): Either[ParserError, L] = parser.apply(program)
}

/** Represents a parser that converts an input string into a logic AST type. */
abstract class Parser[L : Logic] extends RegexParsers {
  def apply(program: String): Either[ParserError, L]
}

/** Represents a parsing error. */
case class ParserError(msg: String) {
  override def toString(): String =
    msg
}
