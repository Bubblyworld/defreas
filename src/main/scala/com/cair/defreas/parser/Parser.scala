package com.cair.defreas
package parser

import scala.util.parsing.combinator._
import scala.collection.mutable.Map

/** Represents an AST for some kind of logic. The specific structure of the
 *  AST is logic-dependent, and the implementation is left to the user.
 */
trait LogicFormula {
  def id(): String
}

/** Represents a parser for a given logic. This is independent of Logic as a
 *  given logic may have a number of possible input formats.
 */
trait LogicParser extends RegexParsers {
  def id(): String
  def parser(): Parser[LogicFormula]
}

/** Registry of logic implementations available to this instance of DefReas.
 *  Pragmas in the knowledge base source file are used to determine which
 *  parser to use for the source file.
 */
object LogicRegistry {
  private val registry: Map[String, LogicParser] = Map.empty

  def register(parser: LogicParser): Unit = registry.addOne(parser.id -> parser)
  def lookup(id: String): Option[LogicParser] = registry.get(id)
}

/** Represents a header in a knowledge base source file. Used for configuring
 *  the logic parser at the moment.
 */
case class Pragma(value: String)

/** Parser for knowledge base source files. The particular LogicParser used
 *  to parse the formulas in the source file is configured using pragma
 *  headers.
 */
class Parser extends RegexParsers {
  def string: Parser[String] = """[a-zA-Z_]+""".r ^^ { _.toString }
  def pragma: Parser[Pragma] = "#" ~> string ^^ { Pragma(_) }
  def eof: Parser[String] = "\\z".r | failure("unexpected character")

  def program: Parser[List[LogicFormula]] = pragma >> {
    case Pragma(value) => LogicRegistry.lookup(value) match {
      case None => failure(s"unknown logic parser: $value")
      case Some(logicParser) => {
        rep(logicParser.parser.asInstanceOf[Parser[LogicFormula]])
      }
    }
  } <~ eof
}

