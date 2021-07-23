package com.cair.defreas.types

import scala.util.parsing.combinator._

/** Represents an input format, or syntax, for a particular logic. */
class Syntax[L : Logic](id: String, parser: Parser[L]) {
  def parse(prog: String): Either[ParserError, L] = parser.apply(prog)
}

/** Represents a parser that converts an input string into a logic AST type. */
abstract class Parser[L : Logic] extends RegexParsers {
  def apply(prog: String): Either[ParserError, L]
}

/** Represents a parsing error. */
class ParserError(msg: String)
