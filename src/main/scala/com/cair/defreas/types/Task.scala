package com.cair.defreas.types

/** Represents a reasoning task that can be performed for a given logic,
 *  taking as input a value of type A and returning as output a value of
 *  type B. */
class Task[L : Logic, A, B](id: String, fn: A => B)(
  implicit inputEvidence: TaskInput[L, A], outputEvidence: TaskOutput[L, B])
  extends TaskWrapper[L] {

  def id(): String =
    id

  // TODO handle errors
  def run(ctx: TaskContext[L]): TaskContext[L] = {
    val res = inputEvidence
      .readFrom(ctx)
      .map(fn)
      .map(outputEvidence.write(_))

    res match {
      case None => throw new Exception("failed to run task")
      case Some(output) => output
    }
  }

  def unwrap(handler: TaskHandler[L]): Unit =
    handler.handle(this)
}

/** Type eraser for Task to allow for polymorphic Package instances. */
sealed trait TaskWrapper[L] {
  def unwrap(handler: TaskHandler[L]): Unit
}

/** Universally-typed handler for Task instances. */
trait TaskHandler[L] {
  def handle[A, B](task: Task[L, A, B]): Unit
}

/** Represents a set of values that can be read from or written to by a Task. */
case class TaskContext[L : Logic](
  bool: Option[Boolean],
  formula: Option[L],
  knowledgeBase: Option[List[L]]
) {
  def this() =
    this(None, None, None)

  def this(bool: Boolean) =
    this(Option(bool), None, None)

  def this(formula: L) =
    this(None, Option(formula), None)

  def this(knowledgeBase: List[L]) =
    this(None, None, Option(knowledgeBase))
}

/** Typeclass for possible Task input values. TaskInput values are given by
 *  an input context, which is a set of values that have been parsed from a 
 *  knowledge base source file or provided by the user. */
sealed trait TaskInput[L, A] {
  def readFrom(ctx: TaskContext[L]): Option[A]
}

/** Typeclass for possible Task output values. TaskOutput values are writted to
 *  an output context at the end of task execution to allow for composition. */
sealed trait TaskOutput[L, A] {
  def write(value: A): TaskContext[L]
}

/** Typeclass instances for Task input/output values. */
object TaskInstances {
  implicit def booleanTaskInput[L : Logic]: TaskInput[L, Boolean] =
    new TaskInput[L, Boolean] {
      def readFrom(ctx: TaskContext[L]): Option[Boolean] =
        ctx.bool
    }

  implicit def knowledgeBaseTaskInput[L : Logic]: TaskInput[L, List[L]] =
    new TaskInput[L, List[L]] {
      def readFrom(ctx: TaskContext[L]): Option[List[L]] =
        ctx.knowledgeBase
    }

  implicit def booleanTaskOutput[L : Logic]: TaskOutput[L, Boolean] =
    new TaskOutput[L, Boolean] {
      def write(value: Boolean): TaskContext[L] = {
        return new TaskContext(value)
      }
    }
}
