package com.cair.defreas.rpc

trait Serial[A] extends Serialisable[A] with Deserialisable[A] {
  val id: String

  // For type-erasure when storing inhomogenous serials.
  trait Handler {
    def handle[A](serial: Serial[A]): Unit
  }

  def unwrap(handler: Handler) =
    handler.handle(this)
}

trait Serialisable[A] {
  def print(value: A): String
}

trait Deserialisable[A] {
  def parse(str: String): Option[A]
}
