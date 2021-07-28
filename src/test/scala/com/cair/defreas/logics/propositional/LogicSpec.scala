package com.cair.defreas.logics.propositional

import org.scalatest.flatspec._
import org.scalatest.matchers._

import com.cair.defreas.types.{ Logic => _, _ }
import Logic._

class LogicSpec extends AnyFlatSpec with should.Matchers {
  "A PropositionalLogic" should "simplify formulas correctly" in {
    val testCases = List(
      (neg(atom("a")), neg(atom("a"))),
      (neg(neg(atom("a"))), atom("a")),
      (neg(neg(neg(atom("a")))), neg(atom("a"))),
      (and(atom("a"), neg(neg(atom("b")))), and(atom("a"), atom("b"))),
    )

    for ((testCase, expected) <- testCases) {
      Util.simplify(testCase, false) shouldBe expected
    }
  }

  it should "convert to CNF form correctly" in {
    val testCases = List[(Logic, Logic)](
      //  Base cases:
      (atom("a"), atom("a")),
      (neg(atom("a")), neg(atom("a"))),
      (or(atom("a"), atom("b")), or(atom("a"), atom("b"))),
      (and(atom("a"), atom("b")), and(atom("a"), atom("b"))),

      // More complex examples:
      (or(atom("a"), and(atom("b"), atom("c"))), and(or(atom("a"), atom("b")), or(atom("a"), atom("c")))),
    )

    for ((testCase, expected) <- testCases) {
      Util.toCNF(testCase) shouldBe expected
    }
  }
}
