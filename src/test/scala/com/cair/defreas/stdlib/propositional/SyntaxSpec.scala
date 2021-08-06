package com.cair.defreas.stdlib.propositional

import org.scalatest.flatspec._
import org.scalatest.matchers._

import com.cair.defreas.types._
import PropositionalLogic._

class SyntaxSpec extends AnyFlatSpec with should.Matchers {
  "A StandardParser" should "correctly parse well-formed formulas" in {
    val wffs = List(
      // Test spaces and basic syntax for operators:
      ("a", atom("a")),
      ("! a", neg(atom("a"))),
      (( "a ^b"), and(atom("a"), atom("b"))),
      ("a| b ", or(atom("a"), atom("b"))),
      ("a -> b", implies(atom("a"), atom("b"))),
      ("abcABC_", atom("abcABC_")),
      ("(a^b)|c", or(and(atom("a"), atom("b")), atom("c"))),
      
      // Test that negation binds most tightly:
      ("!a^b", and(neg(atom("a")), atom("b"))),
      ("!a|b", or(neg(atom("a")), atom("b"))),
      ("!a->b", implies(neg(atom("a")), atom("b"))),

      // Test that implication binds weakest:
      ("a^b->c", implies(and(atom("a"), atom("b")), atom("c"))),
      ("a|b->c", implies(or(atom("a"), atom("b")), atom("c"))),
    )

    for ((str, wff) <- wffs) {
      val res = DefaultSyntax.parse(str)
      res.getOrElse(atom("ERROR")) shouldBe wff
    }
  }

  it should "fail to parse ill-formed formulas" in {
    val iffs = List(
      // Cannot use special characters in names:
      "a^*",
      "a^#",

      // Cannot chain operators of the same precedence without brackets:
      "!!a",
      "a^b^c",
      "a|b|c",
      "a->b->c",
    )

    for (iff <- iffs) {
      val res = DefaultSyntax.parse(iff)
      res shouldBe a [Left[ParseError, _]]
    }
  }
}
