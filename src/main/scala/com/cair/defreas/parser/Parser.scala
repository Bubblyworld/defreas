package com.cair.defreas
package parser

import scala.util.parsing.combinator._

/* Something simple, switches parser on pragma.
 * # propositional            <- pragma
 * A ^ B                      <- formula
 * C | D
 */

case class Pragma(value: String)

abstract class Formula
object Formula {
  case class StringFormula(s: String) extends Formula
  case class IntFormula(i: Int) extends Formula
}

class Parser extends RegexParsers {
  def string: Parser[String] = """[a-zA-Z_]+""".r ^^ { _.toString }
  def int: Parser[Int] = """[1-9][0-9]*""".r ^^ { _.toInt }
  def pragma: Parser[Pragma] = "#" ~> string ^^ { Pragma(_) }
  def eof: Parser[String] = "\\z".r | failure("unexpected character")

  def program: Parser[List[Formula]] = pragma >> {
    case Pragma("string") => rep(string ^^ { Formula.StringFormula(_) })
    case Pragma("int") => rep(int ^^ { Formula.IntFormula(_) })
    case Pragma(x) => failure(s"invalid formula type: $x")
  } <~ eof
}

