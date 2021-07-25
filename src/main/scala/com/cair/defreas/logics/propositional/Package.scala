package com.cair.defreas.logics.propositional

import com.cair.defreas.types.{ Logic => LogicT, _ }

import Instances._

object Package {
  def apply(): Package = {
    val pkg = new Package("propositional_logic")
    pkg.addSyntax[Logic](new Syntax("standard", Parser))

    return pkg
  }
}
