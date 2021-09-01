package com.cair.defreas.rpc

import scala.reflect.runtime.universe._

import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

sealed trait Value[A] {
  val schema: Json

  def codec(reg: Registry): ValueCodec[A]
}

sealed trait ValueCodec[A] extends Encoder[A] with Decoder[A]

object Value {
  val boolean =
    new Value[Boolean] {
      val schema = primitiveSchema("boolean", "A 'boolean' literal.")

      def codec(reg: Registry) =
        new ValueCodec[Boolean]{
          def apply(value: Boolean) =
            implicitly[Encoder[Boolean]].apply(value)

          def apply(c: HCursor) =
            implicitly[Decoder[Boolean]].apply(c)
        }
    }

  val string =
    new Value[String] {
      val schema = primitiveSchema("string", "A 'string' literal.")

      def codec(reg: Registry) =
        new ValueCodec[String] {
          def apply(value: String) =
            implicitly[Encoder[String]].apply(value)

          def apply(c: HCursor) =
            implicitly[Decoder[String]].apply(c)
        }
    }

  val integer =
    new Value[Integer] {
      val schema = primitiveSchema("integer", "An 'integer' literal.")

      def codec(reg: Registry) =
        new ValueCodec[Integer] {
          def apply(value: Integer) =
            implicitly[Encoder[Integer]].apply(value)

          def apply(c: HCursor) =
            implicitly[Decoder[Integer]].apply(c)
        }
    }

  def pair[A, B](fst: Value[A], snd: Value[B]) =
    new Value[(A, B)] {
      val schema = pairSchema(fst, snd)

      def codec(reg: Registry) =
        new ValueCodec[(A, B)] {
          def apply(value: (A, B)) =
            internal(
              fst.codec(reg).apply(value._1),
              snd.codec(reg).apply(value._2)
            ).asJson

          def apply(c: HCursor): Decoder.Result[(A, B)] =
            implicitly[Decoder[internal]].apply(c) match {
              case Left(err) => Left(err)
              case Right(int) => {
                fst.codec(reg).decodeJson(int.fst) match {
                  case Left(err) => Left(err)
                  case Right(valueA) => {
                    snd.codec(reg).decodeJson(int.snd) match {
                      case Left(err) => Left(err)
                      case Right(valueB) => Right(valueA, valueB)
                    }
                  }
                }
              }
            }
        }

      case class internal(fst: Json, snd: Json)
    }

  def serial[A : TypeTag](id: String, serial: Serial[A]) =
    new Value[A] {
      val schema = serialisableSchema(id, serial)

      def codec(reg: Registry) =
        new ValueCodec[A] {
          def apply(value: A): Json =
            internal(
              serial.id,
              serial.print(value)
            ).asJson

          def apply(c: HCursor): Decoder.Result[A] = 
            implicitly[Decoder[internal]].apply(c) match {
              case Left(err) => Left(err)
              case Right(int) => reg
                .getSerialFor[A](int.serial)
                .flatMap(serial => serial.parse(int.value)) match {
                  case None => Left(DecodingFailure("serialisation error", List.empty))
                  case Some(value) => Right(value)
                }
            }
        }

      case class internal(serial: String, value: String)
    }

  def primitiveSchema(tipe: String, description: String) =
    JsonObject
      .empty
      .add("type", tipe.asJson)
      .add("description", description.asJson)
      .asJson

  def pairSchema[A, B](fst: Value[A], snd: Value[B]) =
    JsonObject
      .empty
      .add("description", "A 'pair' of values.".asJson)
      .add("fst", fst.schema)
      .add("snd", snd.schema)
      .asJson

  def serialisableSchema[A](id: String, serial: Serial[A]) =
    JsonObject
      .empty
      .add("description", s"A '${id}' value that can be encoded and decoded with a serial.".asJson)
      .add("serial", s"The ID of the serial to use.".asJson)
      .add("value",
        primitiveSchema(
          "string",
          s"A serialised representation of a '${id}' value."
        )
      ).asJson
}
