package com.cair.defreas.types

import scala.reflect.runtime.universe._

/** Represents a collection of related logics, valid forms of syntax for those
 *  logics and reasoning functions that can be performed on the logics. Note
 *  that Package instances are not referentially transparent, as they contain
 *  mutable values. */
sealed case class Package(
  /** The identifier used to differentiate this package from others. */
  id: String,

  tasks: Map[String, Task.Wrapper],
  syntaxes: DependentMap[String, Syntax],
) {
  /** Adds a Task instance to the package. */
  def addTask(task: Task[_, _]): Package =
    Package(id, tasks + (task.id -> task), syntaxes)

  /** Adds a Syntax instance to the package. */
  def addSyntax(syntax: Syntax[_]): Package =
    Package(id, tasks, syntaxes + (syntax.id -> syntax))

  /** Checks if there exists a Task with the given id in the package. */
  def containsTask(id: String): Boolean =
    tasks.contains(id)

  /** Checks if there exists a Syntax with the given id in the package. */
  def containsSyntax(id: String): Boolean =
    syntaxes.contains(id)

  /** Returns the task with the given id in type-erased form. To use the task, 
   *  call task.unwrap() with an appropriate Task.Handler. */
  def getTask(id: String): Either[NoSuchTaskError, Task.Wrapper] =
    tasks.get(id) match {
      case None => Left(NoSuchTaskError(this.id, id))
      case Some(task) => Right(task)
    }

  /** Returns the syntax with the given id in type-erased form. To use the
   *  syntax, call syntax.unwrap() with an appropriate Syntax.Handler. */
  def getSyntax(id: String): Either[NoSuchSyntaxError, Syntax.Wrapper] =
    syntaxes.get(id) match {
      case None => Left(NoSuchSyntaxError(this.id, id))
      case Some(syntax) => Right(syntax)
    }

  /** Returns the ids of all tasks contained in this package. */
  def listTasks(): List[String] =
    tasks.keys.toList

  /** Returns the ids of all syntaxes for the given type. */
  def listSyntaxes[A : TypeTag](): List[String] =
    syntaxes.keys[A]
}

object Package {
  def apply(id: String): Package =
    new Package(id, Map.empty, DependentMap.empty)
}

/** An error that may occur while working with a Package. */
sealed trait PackageError extends Error

case class UnwrapTaskError(pkgID: String, taskID: String, err: TaskError) extends PackageError {
  override def toString() =
    s"error while unwrapping task '${taskID}' in package '${pkgID}': ${err}"
}

case class NoSuchTaskError(pkgID: String, taskID: String) extends PackageError {
  override def toString() = 
    s"no task with id '${taskID}' exists in package '${pkgID}'"
}

case class NoSuchSyntaxError(pkgID: String, syntaxID: String) extends PackageError {
  override def toString() =
    s"no syntax withid '${syntaxID}' exists in package '${pkgID}'"
}
