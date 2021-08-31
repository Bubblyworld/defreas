package com.cair.defreas.types

import scala.reflect.runtime.universe._

/** Predefined typeclass instances of Value. */
object instances {
  implicit val booleanValue: Value[Boolean] =
    new Value[Boolean] {
      val id = "Boolean"

      def requirements() =
        ValueEnvironment(id, "any")

      def deserialise(
        env: Environment,
        syntaxes: DependentMap[String, Syntax]
      ) =
        deserialiseValue[Boolean](id, env, syntaxes)

      def serialise(
        value: Boolean,
        syntaxes: DependentMap[String, Syntax]
      ) = 
        serialiseValue[Boolean](id, value, syntaxes)
    }

  implicit val stringValue: Value[String] =
    new Value[String] {
      val id = "String"

      def requirements() =
        ValueEnvironment(id, "any")

      def deserialise(
        env: Environment,
        syntaxes: DependentMap[String, Syntax]
      ) =
        deserialiseValue[String](id, env, syntaxes)

      def serialise(
        value: String,
        syntaxes: DependentMap[String, Syntax]
      ) = 
        serialiseValue[String](id, value, syntaxes)
    }

  implicit val intValue: Value[Int] =
    new Value[Int] {
      val id = "Integer"

      def requirements() =
        ValueEnvironment(id, "any")

      def deserialise(
        env: Environment,
        syntaxes: DependentMap[String, Syntax]
      ) =
        deserialiseValue[Int](id, env, syntaxes)

      def serialise(
        value: Int,
        syntaxes: DependentMap[String, Syntax]
      ) = 
        serialiseValue[Int](id, value, syntaxes)
    }

  implicit def logicValue[A](instance: Logic[A]): Value[A] =
    new Value[A] {
      val id = instance.id

      def requirements() =
        ValueEnvironment(id, "any")

      def deserialise(
        env: Environment,
        syntaxes: DependentMap[String, Syntax]
      ) =
        ???

      def serialise(
        value: A,
        syntaxes: DependentMap[String, Syntax]
      ) = 
        ???
    }

  /** Parses a value from the given environment using the specified syntax. */
  def deserialiseValue[A](
    valueID: String,
    env: Environment,
    syntaxes: DependentMap[String, Syntax],
  )(implicit tag: TypeTag[A]): Either[ValueError, A] =
    env match {
      case ObjectEnvironment(_) =>
        Left(InvalidEnvironmentError(valueID))

      case ValueEnvironment(value, syntaxID) =>
        syntaxes.get[A](syntaxID) match {
          case None => Left(NoSuchSyntaxValueError(syntaxID))
          case Some(syntax) => syntax.parse(value) match {
            case Left(err) => Left(SerialiseError(valueID))
            case Right(res) => Right(res)
          }
        }
    }

  /** Prints a value to an environment using whatever syntax can be found. */
  def serialiseValue[A](
    valueID: String,
    value: A,
    syntaxes: DependentMap[String, Syntax]
  )(implicit tag: TypeTag[A]): Either[ValueError, Environment] = {
    val syntaxIDs = syntaxes.keys[A]
    if (syntaxIDs.length == 0)
      return Left(NoSuchSyntaxValueError("any"))

    val syntaxID = syntaxIDs(0)
    syntaxes.get[A](syntaxID) match {
      case None => Left(NoSuchSyntaxValueError(syntaxID))
      case Some(syntax) => 
        Right(ValueEnvironment(syntax.print(value), syntaxID))
    }
  }
}
