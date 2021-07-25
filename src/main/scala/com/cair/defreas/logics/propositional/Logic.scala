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

/** Utility for constructing formulas. */
object Util {
  def atom(label: String): Logic =
    new Atom(label)

  def not(op: Logic): Logic =
    new Negation(op)

  def and(opl: Logic, opr: Logic): Logic =
    new Conjunction(opl, opr)

  def or(opl: Logic, opr: Logic): Logic =
    not(and(not(opl), not(opr)))

  def implies(opl: Logic, opr: Logic): Logic =
    or(not(opl), opr)

  def equiv(opl: Logic, opr: Logic): Logic =
    and(implies(opl, opr), implies(opr, opl))
}

/** Typeclass instances for propositional logic Logics. */
object Instances {
  implicit val logicInstance: LogicT[Logic] =
    new LogicT[Logic] {
      val id: String = "propositional_logic"
    }
}
