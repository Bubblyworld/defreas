package com.cair.defreas.logics.propositional

import com.cair.defreas.types.{ Logic => LogicT, Parser => ParserT, _ }

import Instances._

/** Parser for a simple propositional logic syntax, in which atoms are
 *  represented by strings of uppercase characters, negation is represented
 *  by '!' and conjunction is represented by '&'. */
object StandardParser extends ParserT[Logic] {
  def apply(program: String): Either[ParserError, Logic] =
    parseAll(formula, program) match {
      case Success(res, _) => Right(res)
      case err: NoSuccess => Left(new ParserError(err.msg))
    }

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
