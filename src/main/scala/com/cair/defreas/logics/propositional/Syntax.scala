package com.cair.defreas.logics.propositional

import com.cair.defreas.types.{ Logic => LogicT, Parser => ParserT, _ }

import Instances._

/** ParserT for a simple propositional logic format, in which atoms are
 *  represented by strings of uppercase characters, negation is represented
 *  by '!' and conjunction is represented by '&'. */
object Parser extends ParserT[Logic] {
  def apply(program: String): Either[ParserError, Logic] =
    ???

  def label: Parser[String] = 
    """[A-Z]+""".r ^^ { _.toString }

  def formula: Parser[Logic] = 
    atom | not | and | or

  def atom: Parser[Logic] = 
    label ^^ { Util.atom(_) }

  def not: Parser[Logic] = 
    "!" ~> "(" ~> formula <~ ")" ^^ { Util.not(_) }

  def and: Parser[Logic] = 
    ("(" ~> formula <~ "&") ~ (formula <~ ")") ^^ {
      case opl ~ opr => Util.and(opl, opr)
    }

  def or: Parser[Logic] = 
    ("(" ~> formula <~ "|") ~ (formula <~ ")") ^^ {
      case opl ~ opr => Util.or(opl, opr)
    }
}
