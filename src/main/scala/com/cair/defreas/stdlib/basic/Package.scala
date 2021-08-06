package com.cair.defreas.stdlib.basic

import com.cair.defreas.types._

object getPackage {
  def apply() =
    Package("stdlib_basic")
      .addSyntax(IntegerSyntax)
      .addSyntax(BooleanSyntax)
      .addSyntax(StringSyntax)
}
