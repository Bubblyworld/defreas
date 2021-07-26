package com.cair.defreas.logics.propositional

import com.cair.defreas.types.{ Logic => LogicT, Parser => ParserT, _ }
import com.cair.defreas.types.TaskInstances._

import Instances._

/** Container for predefined propostional logic tasks. */
object Tasks {
  val isSatisfiable = new Task[Logic, List[Logic], Boolean](
    "is_satisfiable",
    kb => true, // TODO: actual implementation
  )
}
