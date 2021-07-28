package com.cair.defreas.logics.propositional

import com.cair.defreas.types.{ Logic => LogicT }

/** Represents an Logic for propositional logic. */
sealed trait Logic

/** Logic type for a propositional atom. */
final case class Atom(label: String) extends Logic

/** Logic type for negations of propositional formulas. */
final case class Negation(op: Logic) extends Logic

/** Logic type for conjunctions of propositional formulas. */
final case class Conjunction(opl: Logic, opr: Logic) extends Logic

/** Logic type for disjunctions of propositional formulas. */
final case class Disjunction(opl: Logic, opr: Logic) extends Logic

/** Logic type for implications of propositional formulas. */
final case class Implication(opl: Logic, opr: Logic) extends Logic

object Logic {
  def atom(label: String): Logic =
    new Atom(label)

  def neg(op: Logic): Logic =
    new Negation(op)

  def and(opl: Logic, opr: Logic): Logic =
    new Conjunction(opl, opr)

  def or(opl: Logic, opr: Logic): Logic =
    new Disjunction(opl, opr)

  def implies(opl: Logic, opr: Logic): Logic =
    new Implication(opl, opr)

  def equiv(opl: Logic, opr: Logic): Logic =
    and(implies(opl, opr), implies(opr, opl))
}

/** Typeclass instances for propositional logic. */
object Instances {
  implicit val logicInstance: LogicT[Logic] =
    new LogicT[Logic] {
      val id: String = "propositional_logic"
    }
}

/** Utility functions for manipulating propositional formulas. */
object Util {
  import Logic._

  /** Converts a propositional formula to conjunctive normal form. */
  def toCNF(formula: Logic): Logic =
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
  def simplify(formula: Logic, negating: Boolean): Logic =
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
