package com.cair.defreas.types

/** Represents a collection of related logics, valid forms of syntax for those
 *  logics and reasoning functions that can be performed on the logics. */
class Package(name: String) {
  private val taskMap = new NamespacedMap[String, TaskWrapper]()

  /** Adds a Task instance to the package. */
  def addTask[L : Logic, A, B](id: String, task: Task[L, A, B]): Unit =
    taskMap.add[L](id, task)

  /** Checks if there exists a Task with the given id in the package. */
  def hasTask[L : Logic](id: String): Boolean =
    taskMap.has[L](id)

  /** Runs the Task with the given id in the given context. */
  def runTask[L : Logic](id: String, handler: TaskHandler[L]): Unit =
    taskMap.get[L](id).map(_.unwrap(handler))
}

/** Represents a map of instances of F[L], where L is a universal logic type,
 *  indexed by the logic type and an instance of type K. */
class NamespacedMap[K, F[L]] {
  import scala.collection.mutable.Map

  /* Map from a logic type L and an id of type K to an instance of F[L].
   * Note that Key#LogicT is actually a more general type than we want: the
   * map value type should be F[Key.LogicT], rather than F[Key#LogicT], but 
   * there's no way to express this dependent type in Scala. We get around 
   * this by doing explicit (though logically safe) type casts. */
  private val map = Map[Key, F[Key#LogicT]]()

  def add[L : Logic](id: K, value: F[L]): Unit = {
    val key = makeKey[L](id)
    map.addOne(key -> value.asInstanceOf[F[Key#LogicT]])
  }

  def get[L : Logic](id: K): Option[F[L]] = {
    val key = makeKey[L](id)
    map.get(key).map(_.asInstanceOf[F[L]])
  }

  def has[L : Logic](id: K): Boolean = {
    val key = makeKey[L](id)
    map.contains(key)
  }

  sealed private trait Key extends Equals {
    type LogicT
    val id: K

    override def equals(that: Any): Boolean
  }

  private class InternalKey[L : Logic](_id: K) extends Key {
    type LogicT = L
    val id = _id

    def canEqual(that: Any): Boolean =
      that.isInstanceOf[InternalKey[L]]

    override def equals(that: Any): Boolean = {
      if (!canEqual(that)) return false

      val key = that.asInstanceOf[InternalKey[L]]
      return this.id == key.id
    }

    // This hashCode will cause collisions when there are tasks with the same
    // id but different logic types. This shouldn't happen often in practice.
    override def hashCode(): Int =
      this.id.hashCode()
  }

  private def makeKey[L : Logic](id: K): Key =
    new InternalKey[L](id)
}
