package com.cair.defreas.types

/** A reasoning task that maps Values of A to Values of B. */
case class Task[A : Value, B : Value](
  id: String,
  fn: A => Either[ExecutionError, B],
) extends Task.Wrapper {
  def run(
    env: Environment,
    syntaxes: DependentMap[String, Syntax]
  ): Either[TaskError, Environment] = {
    val aValue = implicitly[Value[A]]
    val bValue = implicitly[Value[B]]

    aValue.deserialise(env, syntaxes) match {
      case Left(err) => Left(SerialisationError(err.toString()))
      case Right(value) => fn(value).flatMap(
        bValue.serialise(_, syntaxes) match {
          case Left(err) => Left(SerialisationError(err.toString()))
          case Right(res) => Right(res)
        }
      )
    }
  }

  def unwrap[C](handler: Task.Handler[C]): C =
    handler.handle(this)
}

object Task {
  /** Existential type erasor for Task types. */
  sealed trait Wrapper {
    def unwrap[C](handler: Handler[C]): C
  }
  
  /** Existential type handler for Task types. */
  trait Handler[C] {
    def handle[A : Value, B : Value](task: Task[A, B]): C
  }
}

/** An error that may occur while working with a Task. */
sealed trait TaskError extends Error

/** An error that may occur during Task input/output serialisation. */
case class SerialisationError(msg: String) extends TaskError {
  override def toString() = msg
}

/** An error that may occur during Task execution. */
case class ExecutionError(msg: String) extends TaskError {
  override def toString() = msg
}

