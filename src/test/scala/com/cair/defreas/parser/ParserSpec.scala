package com.cair.defreas
package parser

import org.scalatest.flatspec._
import org.scalatest.matchers._
import scala.util.parsing.combinator._

class ParserSpec extends AnyFlatSpec with should.Matchers {
  val p = new Parser()

  "A Parser" should "parse a valid string" in {
    val res = p.parse(p.string, "test_Test")
    res shouldBe a [p.Success[_]]
    res.get shouldBe "test_Test"
  }

  it should "not parse an invalid string" in {
    val res = p.parse(p.string, "1234")
    res shouldBe a [p.Failure]
  }

  it should "parse a valid int" in {
    val res = p.parse(p.int, "1234")
    res shouldBe a [p.Success[_]]
    res.get shouldBe 1234
  }
  
  it should "not parse an invalid int" in {
    val res = p.parse(p.int, "01234")
    res shouldBe a [p.Failure]
  }

  it should "parse a valid pragma" in {
    val res = p.parse(p.pragma, "# propositional_logic")
    res shouldBe a [p.Success[_]]
    res.get.value shouldBe "propositional_logic"
  }

  it should "parse a valid string program" in {
    val test = """# string
                 |this
                 |is
                 |a
                 |list""".stripMargin('|')

    val res = p.parse(p.program, test)
    res shouldBe a [p.Success[_]]
    res.get shouldBe List("this", "is", "a", "list")
      .map({ Formula.StringFormula(_) })
  }

  it should "parse a valid int program" in {
    val test = """# int
                 |1
                 |2
                 |3
                 |4""".stripMargin('|')

    val res = p.parse(p.program, test)
    res shouldBe a [p.Success[_]]
    res.get shouldBe List(1, 2, 3, 4)
      .map({ Formula.IntFormula(_) })
  }
}
