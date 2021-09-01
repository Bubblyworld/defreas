package com.cair.defreas.rpc

import scala.reflect.runtime.universe._

/** Type of a DependentMap's keys. */
sealed trait Key[K] {
  /** The key's identifier. */
  val id: K

  /** A tag for the type of the key's mapped typeclass instance. */
  val tag: TypeTag[_]

  /** Maps the key's current identifier to a new one. */
  def map(f: K => K): Key[K]
}

/** Represents a map of instances of F[L], where L is a universal logic type,
 *  indexed by the logic type and an instance of type K. */
case class DependentMap[K, F[_]](
  /** Map from a key value of type A to a instance of the typeclass F[_]. */
  data: Map[Key[K], F[_]],
) {
  def +[A](kv: (K, F[A]))(implicit tag: TypeTag[A]): DependentMap[K, F] =
    add(kv._1, kv._2)

  def add[A](k: K, v: F[A])(implicit tag: TypeTag[A]): DependentMap[K, F] =
    DependentMap(data + 
      (KeyImpl[A](k, tag) -> v)) 

  def get[A](id: K)(implicit tag: TypeTag[A]): Option[F[A]] =
    data.get(KeyImpl[A](id, tag)).map(_.asInstanceOf[F[A]])

  def contains[A](id: K)(implicit tag: TypeTag[A]): Boolean =
    data.contains(KeyImpl[A](id, tag))

  def keys[A](implicit tag: TypeTag[A]): List[K] =
    data
      .keys
      .toList
      .filter(_.tag.tpe == tag.tpe)
      .map(_.id)

  def allKeys: List[K] =
    data
      .keys
      .toList
      .map(_.id)

  def ++(map: DependentMap[K, F]): DependentMap[K, F] =
    combine(map)

  def combine(map: DependentMap[K, F]): DependentMap[K, F] =
    DependentMap(data ++ map.data)

  def map(f: ((Key[K], F[_])) => (Key[K], F[_])): DependentMap[K, F] =
    DependentMap(data.map(f))

  private case class KeyImpl[A](id: K, tag: TypeTag[A]) extends Key[K] {
    def map(f: K => K): Key[K] =
      KeyImpl[A](f(id), tag)

    override def equals(that: Any): Boolean = {
      if (!that.isInstanceOf[KeyImpl[_]]) return false
  
      val key = that.asInstanceOf[KeyImpl[A]]
      return id == key.id && tag.tpe =:= key.tag.tpe
    }
  }
}

object DependentMap {
  def empty[K, F[_]] = DependentMap[K, F](Map.empty)
}
