package com.cair.defreas.types

/** Typeclass for something that can be accepted by a Task as an argument or 
 *  computed by a Task as a return value. Value instances need to support 
 *  generic serialisation to/from a TaskEnvironment. */
abstract class Value[A] {
  /** Identifier used for the type A when referenced by a Task. */
  val id: String

  /** Returns a list of named, serialised values an environment must contain 
   *  in order to serialise a Value of this type. The reason we return a list
   *  is to support Value instances for algebraic datatypes. */
  def requirements(): Environment

  /** Serialises a Value to an environment instance. */
  def serialise(
    value: A,
    syntaxes: DependentMap[String, Syntax]
  ): Either[ValueError, Environment]

  /** Deserialises a Value from an environment instance. */
  def deserialise(
    env: Environment,
    syntaxes: DependentMap[String, Syntax]
  ): Either[ValueError, A]
}

sealed trait ValueError extends Error

case class InvalidEnvironmentError(
  valueID: String
) extends ValueError {
  override def toString() =
    s"invalid environment provided for ${valueID} value"
}

case class NoSuchSyntaxValueError(
  syntax: String
) extends ValueError {
  override def toString() =
    s"no syntax with id ${syntax} found in registry"
}

case class SerialiseError(
  valueID: String
) extends ValueError {
  override def toString() =
    s"error while serialising a ${valueID} value from environment"
}

/** A generic runtime environment for Tasks, independent of the source of
 *  user input/output (which may be either the CLI or the web app). Values
 *  are mapped recursively from their names to a object representation. */
sealed trait Environment

case class ValueEnvironment(
  value: String,
  syntax: String
) extends Environment

case class ObjectEnvironment(
  values: Map[String, Environment]
) extends Environment
