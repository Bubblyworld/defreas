package com.cair.defreas.stdlib.basic

import com.cair.defreas.types._

/** Parser/printer for Integer values. */
object IntegerSyntax extends Syntax[Int] {
  val id = "integer"

  def parse(str: String): Either[ParseError, Int] =
    try {
      Right(str.toInt)
    } catch {
      case _: java.lang.NumberFormatException =>
        Left(ParseError(s"tried to parse invalid integer: ${str}"))
    }

  def print(value: Int): String =
    value.toString()
}

