package com.cair.defreas.stdlib.basic

import com.cair.defreas.types._

/** Parser/printer for Boolean values. */
object BooleanSyntax extends Syntax[Boolean] {
  val id = "boolean"

  def parse(str: String): Either[ParseError, Boolean] =
    try {
      Right(str.toBoolean)
    } catch {
      case _: java.lang.IllegalArgumentException =>
        Left(ParseError(s"tried to parse invalid boolean: ${str}"))
    }

  def print(value: Boolean): String =
    value.toString()
}
