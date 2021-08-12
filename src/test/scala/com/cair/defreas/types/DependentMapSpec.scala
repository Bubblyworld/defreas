package com.cair.defreas.types

import org.scalatest.flatspec._
import org.scalatest.matchers._

class DependentMapSpec extends AnyFlatSpec with should.Matchers {
  trait TestF[A] {
    val value: A
  }

  object TestF {
    def apply[A](_value: A): TestF[A] =
      new TestF[A] {
        val value = _value
      }
  }

  "A DependentMap" should "support adding values" in {
    val map = DependentMap
      .empty[String, TestF]
      .add("string_key1", TestF[String]("value1")) +
        ("string_key2" -> TestF[String]("value2"))

    map.keys[String].length shouldBe 2
  }

  it should "correctly check for contained values" in {
    val map = DependentMap
      .empty[String, TestF]
      .add[String]("string_key1", TestF("value1"))
      .add[String]("string_key2", TestF("value2"))
      .add[Int]("int_key", TestF(2))
      .add[Any]("any_key", TestF("random"))

    // Positive checks:
    map.contains[String]("string_key1") shouldBe true
    map.contains[String]("string_key2") shouldBe true
    map.contains[Int]("int_key") shouldBe true

    // Negative checks:
    map.contains[Int]("string_key1") shouldBe false
    map.contains[Int]("string_key2") shouldBe false
    map.contains[String]("int_key") shouldBe false
    map.contains[String]("any_key") shouldBe false
  }
  
  it should "correctly return contained values" in {
    val map = DependentMap
      .empty[String, TestF]
      .add[String]("string_key1", TestF("value1"))
      .add[String]("string_key2", TestF("value2"))
      .add[Int]("int_key", TestF(2))

    // Positive existence checks:
    map.get[String]("string_key1") shouldBe a [Some[_]]
    map.get[String]("string_key2") shouldBe a [Some[_]]
    map.get[Int]("int_key") shouldBe a [Some[_]]

    // Negative existence checks:
    map.get[Any]("string_key1") shouldBe None
    map.get[Int]("string_key2") shouldBe None
    map.get[String]("int_key") shouldBe None

    // Positive value checks:
    map.get[String]("string_key1").get.value shouldBe "value1"
    map.get[String]("string_key2").get.value shouldBe "value2"
    map.get[Int]("int_key").get.value shouldBe 2
  }

  it should "correctly list keys for given types" in {
    val map = DependentMap
      .empty[String, TestF]
      .add[String]("string_key1", TestF("value1"))
      .add[String]("string_key2", TestF("value2"))
      .add[Int]("int_key", TestF(2))

    // Positive checks:
    val keys = map.keys[String]
    keys.length shouldBe 2
    keys should contain("string_key1")
    keys should contain("string_key2")

    // Negative checks:
    map.keys[Any].length should be(0)
  }

  it should "allow for mapping over keys without losing data" in {
    val map = DependentMap
      .empty[String, TestF]
      .add[String]("string_key1", TestF("value1"))
      .add[String]("string_key2", TestF("value2"))
      .add[Int]("int_key", TestF(3))
      .map(
        kv => (kv._1.map(
          id => s"test/${id}"
        ), kv._2)
      )

    // Positive checks:
    var keys = map.keys[String]
    keys.length shouldBe 2
    keys should contain("test/string_key1")
    keys should contain("test/string_key2")

    keys = map.keys[Int]
    keys.length shouldBe 1
    keys should contain("test/int_key")

    // Positive value checks:
    map.get[String]("test/string_key1").get.value shouldBe "value1"
    map.get[String]("test/string_key2").get.value shouldBe "value2"
    map.get[Int]("test/int_key").get.value shouldBe 3
  }

  it should "correctly combine maps without losing data" in {
    val map1 = DependentMap
      .empty[String, TestF]
      .add[String]("string_key1", TestF("value1"))

    val map2 = DependentMap
      .empty[String, TestF]
      .add[String]("string_key2", TestF("value2"))

    // Positive checks:
    val map = map1 ++ map2
    var keys = map.keys[String]
    keys.length shouldBe 2
    keys should contain("string_key1")
    keys should contain("string_key2")
    map.get[String]("string_key1").get.value shouldBe "value1"
    map.get[String]("string_key2").get.value shouldBe "value2"
  }
}
