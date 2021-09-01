package com.cair.defreas.rpc

import scala.reflect.runtime.universe._

case class Registry(
  /** Map from task ID to a type-erased instance of the task. */
  tasks: Map[String, Task[_, _]],

  /** Dependent map from serial IDs to serials of the given type. */
  serials: DependentMap[String, Serial],
) {
  def addTask(task: Task[_, _]) =
    Registry(tasks + (task.id -> task), serials)

  def addSerial[A : TypeTag](serial: Serial[A]) =
    Registry(tasks, serials +[A] (serial.id -> serial))

  def getSerialFor[A : TypeTag](id: String): Option[Serial[A]] =
    serials.get[A](id)

  def allSerialsFor[A : TypeTag](): List[Serial[A]] =
    serials.keys[A].flatMap(id => serials.get[A](id))

  def allSerials(): List[Serial[_]] =
    serials.data.values.toList
}

object Registry {
  val empty = Registry(Map.empty, DependentMap.empty)
}
