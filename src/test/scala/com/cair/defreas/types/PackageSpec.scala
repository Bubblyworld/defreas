package com.cair.defreas.types

import org.scalatest.flatspec._
import org.scalatest.matchers._

class PackageSpec extends AnyFlatSpec with should.Matchers {
  // Example logic AST for testing.
  case class TestLogic() {}
  implicit val testLogicInstance: Logic[TestLogic] =
    new Logic[TestLogic] {
      val id = "test_logic"
    }

  // Typeclass for testing NamespacedMap.
  trait TestF[L] 
  object TestF {
    def apply[L : Logic](): TestF[L] =
      new TestF[L] {}
  }

  "A NamespacedMap" should "support adding values" in {
    val map = new NamespacedMap[String, TestF]()

    val key = "test_key"
    val value = TestF[TestLogic]()
    map.add[TestLogic](key, value)
  }

  it should "support getting values" in {
    val map = new NamespacedMap[String, TestF]()

    val key = "test_key"
    val value = TestF[TestLogic]()
    map.add[TestLogic](key, value)

    // should work if we use the existing key
    val value2 = map.get[TestLogic](key)
    value2 shouldBe a [Some[_]]
    value2.get shouldBe value

    // should work if we create a new key with the same URI
    val key2 = "test_key"
    val value3 = map.get[TestLogic](key2)
    value3 shouldBe a [Some[_]]
    value3.get shouldBe value
  }

  it should "support mapping over keys with a Handler instance" in {
    val map = new NamespacedMap[String, TestF]()

    val key = "test_key"
    val value = TestF[TestLogic]()
    map.add[TestLogic](key, value)

    val key2 = "test_key2"
    val value2 = TestF[TestLogic]()
    map.add[TestLogic](key2, value2)

    var res = List[String]()
    map.map(new map.Handler {
      def handle[L : Logic](id: String, value: TestF[L]): Unit =
        res = res :+ id
    })

    res.length shouldBe 2
    res.contains(key) shouldBe true
    res.contains(key2) shouldBe true
  }
}
