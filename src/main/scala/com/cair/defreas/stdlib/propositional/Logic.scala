package com.cair.defreas.stdlib.propositional

import com.cair.defreas.types._

/** Represents an PropositionalLogic for propositional logic. */
sealed trait PropositionalLogic

/** PropositionalLogic type for a propositional atom. */
final case class Atom(label: String) extends PropositionalLogic {
  override def toString() = label
}

/** PropositionalLogic type for negations of propositional formulas. */
final case class Negation(op: PropositionalLogic) extends PropositionalLogic {
  override def toString() = s"!${op}"
}

/** PropositionalLogic type for conjunctions of propositional formulas. */
final case class Conjunction(opl: PropositionalLogic, opr: PropositionalLogic) extends PropositionalLogic {
  override def toString() = s"(${opl} ^ ${opr})"
}

/** PropositionalLogic type for disjunctions of propositional formulas. */
final case class Disjunction(opl: PropositionalLogic, opr: PropositionalLogic) extends PropositionalLogic {
  override def toString() = s"(${opl} | ${opr})"
}

/** PropositionalLogic type for implications of propositional formulas. */
final case class Implication(opl: PropositionalLogic, opr: PropositionalLogic) extends PropositionalLogic {
  override def toString() = s"(${opl} -> ${opr})"
}

object PropositionalLogic {
  def atom(label: String): PropositionalLogic =
    new Atom(label)

  def neg(op: PropositionalLogic): PropositionalLogic =
    new Negation(op)

  def and(opl: PropositionalLogic, opr: PropositionalLogic): PropositionalLogic =
    new Conjunction(opl, opr)

  def or(opl: PropositionalLogic, opr: PropositionalLogic): PropositionalLogic =
    new Disjunction(opl, opr)

  def implies(opl: PropositionalLogic, opr: PropositionalLogic): PropositionalLogic =
    new Implication(opl, opr)

  def equiv(opl: PropositionalLogic, opr: PropositionalLogic): PropositionalLogic =
    and(implies(opl, opr), implies(opr, opl))
}

/** Typeclass instances for propositional logic. */
object instances {
  implicit val logicInstance: Logic[PropositionalLogic] =
    new Logic[PropositionalLogic] {
      val id: String = "propositional"
    }
}

/** Utility functions for manipulating propositional formulas. */
object Util {
  import PropositionalLogic._

  /** Converts a propositional formula to conjunctive normal form. */
  def toCNF(formula: PropositionalLogic): PropositionalLogic =
    simplify(formula, false) match {
      case Atom(l) => atom(l)
      case Negation(a) => neg(a)
      case Conjunction(a, b) => and(toCNF(a), toCNF(b))
      case Disjunction(a, b) =>
        (toCNF(a), toCNF(b)) match {
          case (a1, Conjunction(b1, b2)) => toCNF(and(or(a1, b1), or(a1, b2))) 
          case (Conjunction(a1, a2), b1) => toCNF(and(or(a1, b1), or(a2, b1)))
          case (a1, b1) => or(a1, b1)
        }

      // This can only happen if there is an implementation bug!
      case _ => throw new Exception("impossible toCNF() state")
    }

  /** Returns equivalent with no implications and only atomic negations. */
  def simplify(formula: PropositionalLogic, negating: Boolean): PropositionalLogic =
    formula match {
      case Atom(l) => if (negating) neg(atom(l)) else atom(l)
      case Negation(a) => simplify(a, !negating)
      case Implication(a, b) => or(simplify(a, !negating), simplify(b, negating))
      case Conjunction(a, b) =>
        if (negating) or(simplify(a, true), simplify(b, true))
        else and(simplify(a, false), simplify(b, false))
      case Disjunction(a, b) =>
        if (negating) and(simplify(a, true), simplify(b, true))
        else or(simplify(a, false), simplify(b, false))
    }
}
