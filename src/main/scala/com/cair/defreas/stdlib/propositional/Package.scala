package com.cair.defreas.stdlib.propositional

import com.cair.defreas.types._
import com.cair.defreas.types.instances._
import instances._

object getPackage {
  def apply() =
    Package("stdlib_propositional")
      .addSyntax(DefaultSyntax)
      .addTask(Tasks.printInt)
}
