package com.cair.defreas.stdlib.basic

import com.cair.defreas.types._

/** Parser/printer for String values. */
object StringSyntax extends Syntax[String] {
  val id = "string"

  def parse(str: String): Either[ParseError, String] =
    Right(str)

  def print(value: String): String =
    value
}

