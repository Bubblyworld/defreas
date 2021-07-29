package com.cair.defreas.logics.propositional

import com.cair.defreas.types.{ Logic => LogicT, Parser => ParserT, _ }

import Instances._

/** Container for predefined propostional logic tasks. */
object Tasks {
  // TODO: actual implementation
  val isSatisfiable: List[Logic] => Boolean =
    _ => true

  val toCNF: List[Logic] => List[Logic] =
    _.map(Util.toCNF)
}
