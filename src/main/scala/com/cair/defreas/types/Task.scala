package com.cair.defreas.types

/** Represents a reasoning task that can be performed on a knowledge base,
 *  taking as input a knowledge base and additional value of type A (which
 *  may be the unit), and returning a value of type B. Task input and output 
 *  types are encoded using the simple type system defined by Type. */
class Task[L : Logic, A, B](id: String, fn: A => B)(
  implicit inputEvidence: TaskInput[L, A], outputEvidence: TaskOutput[L, B])
  extends TaskWrapper[L] {

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
class TaskContext[L]() {
  var bool: Option[Boolean] = None
  var formula: Option[L] = None
  var knowledgeBase: Option[List[L]] = None
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
        val res = new TaskContext[L]()
        res.bool = Option(value)
        return res
      }
    }
}
