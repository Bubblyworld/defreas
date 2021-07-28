package com.cair.defreas.logics.propositional

import com.cair.defreas.types.{ Logic => LogicT, _ }
import com.cair.defreas.types.TaskInstances._

import Instances._

object Package {
  def apply(): Package = {
    val pkg = new Package("stdlib_prop")
    pkg.addSyntax(new Syntax("standard", StandardParser))
    pkg.addTask(new Task("is_satisfiable", Tasks.isSatisfiable))
    pkg.addTask(new Task("to_cnf", Tasks.toCNF))

    return pkg
  }
}
