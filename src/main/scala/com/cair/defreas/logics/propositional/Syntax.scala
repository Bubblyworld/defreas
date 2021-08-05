package com.cair.defreas.logics.propositional

import scala.util.parsing.combinator._

import com.cair.defreas.types._
import instances._

/** Parser for a simple propositional logic syntax, in which atoms are
 *  represented by strings of uppercase characters, negation is represented
 *  by '!' and conjunction is represented by '^'. */
object DefaultSyntax extends Syntax[PropositionalLogic] with RegexParsers {
  val id = "default"

  def parse(str: String): Either[ParseError, PropositionalLogic] =
    parse(phrase(wff), str) match {
      case _ : NoSuccess => Left(ParseError("parsing failure in standard"))
      case Success(formula, _) => Right(formula)
    }

  def print(formula: PropositionalLogic): String =
    formula.toString()

  def ident: Parser[String] = 
    """[a-zA-Z_]+""".r ^^ { _.toString }

  def wff: Parser[PropositionalLogic] =
    wff1 ~ opt("->" ~> wff1) ^^ {
      case opl ~ None => opl
      case opl ~ Some(opr) => PropositionalLogic.implies(opl, opr)
    }

  def wff1: Parser[PropositionalLogic] = {
    val subwff1 = 
      ("^" ~> wff2 ^^ { x => PropositionalLogic.and(_, x) }) | 
      ("|" ~> wff2 ^^ { x => PropositionalLogic.or(_, x) })

    wff2 ~ opt(subwff1) ^^ {
      case opl ~ None => opl
      case opl ~ Some(fn) => fn(opl)
    }
  }

  def wff2: Parser[PropositionalLogic] =
    atom |
    ("!" ~> atom ^^ { PropositionalLogic.neg(_) }) |
    ("!(" ~> wff <~ ")") ^^ { PropositionalLogic.neg(_) } |
    ("(" ~> wff <~ ")")

  def atom: Parser[PropositionalLogic] = 
    ident ^^ { PropositionalLogic.atom(_) }
}
