package com.cair.defreas.logics.propositional

import com.cair.defreas.types.{ Logic => LogicT, Parser => ParserT, _ }

import Instances._

/** Parser for a simple propositional logic syntax, in which atoms are
 *  represented by strings of uppercase characters, negation is represented
 *  by '!' and conjunction is represented by '^'. */
object StandardParser extends ParserT[Logic] {
  def apply(formulaStr: String): Either[ParserError, Logic] =
    parseAll(wff, formulaStr) match {
      case Success(res, _) => Right(res)
      case err: NoSuccess => Left(new ParserError(err.msg))
    }

  def ident: Parser[String] = 
    """[a-zA-Z_]+""".r ^^ { _.toString }

  def wff: Parser[Logic] =
    wff1 ~ opt("->" ~> wff1) ^^ {
      case opl ~ None => opl
      case opl ~ Some(opr) => Logic.implies(opl, opr)
    }

  def wff1: Parser[Logic] = {
    val subwff1 = 
      ("^" ~> wff2 ^^ { x => Logic.and(_, x) }) | 
      ("|" ~> wff2 ^^ { x => Logic.or(_, x) })

    wff2 ~ opt(subwff1) ^^ {
      case opl ~ None => opl
      case opl ~ Some(fn) => fn(opl)
    }
  }

  def wff2: Parser[Logic] =
    atom |
    ("!" ~> atom ^^ { Logic.neg(_) }) |
    ("(" ~> wff <~ ")")

  def atom: Parser[Logic] = 
    ident ^^ { Logic.atom(_) }
}
