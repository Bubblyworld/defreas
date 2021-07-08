package com.cair.defreas
package parser

import org.scalatest.flatspec._
import org.scalatest.matchers._
import scala.util.parsing.combinator._

class ParserSpec extends AnyFlatSpec with should.Matchers {
  // Construct new parser that recognises simple propositional logic.
  val p = new Parser()
  LogicRegistry.register(PropositionalParser)

  "A Parser" should "parse a valid string" in {
    val res = p.parse(p.string, "test_Test")
    res shouldBe a [p.Success[_]]
    res.get shouldBe "test_Test"
  }

  it should "not parse an invalid string" in {
    val res = p.parse(p.string, "1234")
    res shouldBe a [p.Failure]
  }

  it should "parse a valid pragma" in {
    val res = p.parse(p.pragma, "# propositional_logic")
    res shouldBe a [p.Success[_]]
    res.get.value shouldBe "propositional_logic"
  }

  it should "parse a valid propositional program" in {
    val testf1 = PropositionalAtom("A")
    val testf2 = PropositionalNegation(PropositionalAtom("A"))
    val testf3 = PropositionalConjunction(
      PropositionalAtom("A"),
      PropositionalAtom("B"))
    val test = """# propositional_logic_simple
                 |A
                 |!(A)
                 |(A ^ B)""".stripMargin('|')

    val res = p.parse(p.program, test)
    res shouldBe a [p.Success[_]]
    res.get shouldBe List(testf1, testf2, testf3)
  }
}
