package com.cair.defreas
package parser

import scala.util.parsing.combinator._

/** AST type for propositional logic. */
sealed trait PropositionalFormula extends LogicFormula {
  def id(): String = "propositional_logic"
}

/* AST type for a propositional atom. */
final case class PropositionalAtom(label: String) extends PropositionalFormula

/* AST type for formula negation. */
final case class PropositionalNegation(
  op: PropositionalFormula) extends PropositionalFormula

/* AST type for conjunctions of formulas. */
final case class PropositionalConjunction(opl: PropositionalFormula, 
  opr: PropositionalFormula) extends PropositionalFormula

/* Utility for disjunctions of formulas. */
object PropositionalDisjunction extends PropositionalFormula {
  def apply(opl: PropositionalFormula, opr: PropositionalFormula) =
    PropositionalNegation(
      PropositionalConjunction(
        PropositionalNegation(opl),
        PropositionalNegation(opr)))
}

/* Utility for formula implications. */
object PropositionalImplication extends PropositionalFormula {
  def apply(opl: PropositionalFormula, opr: PropositionalFormula) =
    PropositionalDisjunction(
      PropositionalNegation(opl),
      opr)
}

/** Parser for a simple propositional logic format, in which atoms are
 *  represented by strings of uppercase characters, negation is represented
 *  by '!' and conjunction is represented by '&'. */
object PropositionalParser extends LogicParser {
  def id() = "propositional_logic_simple"

  def parser() = atom | neg | conj

  def label = """[A-Z]+""".r ^^ { _.toString }

  def atom = label ^^ { PropositionalAtom(_) }

  def neg = "!" ~> "(" ~> atom <~ ")" ^^ {
    PropositionalNegation(_)
  }

  def conj = ("(" ~> atom <~ "^") ~ (atom <~ ")") ^^ {
    case opl ~ opr => PropositionalConjunction(opl, opr)
  }
}
