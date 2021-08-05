package com.cair.defreas.logics.propositional

import com.cair.defreas.types._
import instances._

/** Container for predefined propostional logic tasks. */
object Tasks {
  val isSatisfiable: List[PropositionalLogic] => Either[ExecutionError, Boolean] =
    kb => Right(true)

  val toCNF: List[PropositionalLogic] => Either[ExecutionError, List[PropositionalLogic]] =
    kb => Right(kb.map(Util.toCNF))

  val printInt = new Task[Int, String](
    "print_int",
    i => Right(i.toString())
  )
}
