package com.cair.defreas.logics.propositional

import com.cair.defreas.types._
import com.cair.defreas.types.instances._
import instances._

object getPackage {
  def apply() =
    Package("stdlib_prop")
      .addSyntax(DefaultSyntax)
      .addTask(Tasks.printInt)
}
