/*
 * Copyright 2018 Artur Zhurat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.azhur.kafkaserdecirce

import java.nio.charset.StandardCharsets
import java.util

import io.circe.{ Decoder, Encoder }
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }

import scala.language.implicitConversions
import scala.util.{ Failure, Success, Try }

trait CirceSupport {
  implicit def circeEncoder2kafkaSerializer[T >: Null](
      implicit encoder: Encoder[T]
  ): Serializer[T] =
    new CirceSerializer[T]()(encoder)

  implicit def circeDecoder2kafkaDeserializer[T >: Null](
      implicit decoder: Decoder[T]
  ): Deserializer[T] =
    new CirceDeserializer[T]()(decoder)

  implicit def circeEncoderDecoder2kafkaSerde[T >: Null](implicit encoder: Encoder[T],
                                                         decoder: Decoder[T]): Serde[T] =
    new Serde[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

      override def close(): Unit = {}

      override def serializer(): Serializer[T] = new CirceSerializer[T]()(encoder)

      override def deserializer(): Deserializer[T] =
        new CirceDeserializer[T]()(decoder)
    }
}

object CirceSupport extends CirceSupport

class CirceSerializer[T](implicit val enc: Encoder[T]) extends Serializer[T] {

  import io.circe.syntax._

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: T): Array[Byte] =
    Try(data.asJson.noSpaces.getBytes) match {
      case Failure(e) =>
        throw new SerializationException("Error serializing JSON message", e)
      case Success(r) => r
    }

  override def close(): Unit = {}
}

class CirceDeserializer[T >: Null](implicit val dec: Decoder[T]) extends Deserializer[T] {

  import io.circe._
  import cats.syntax.either._

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): T = data match {
    case null => null
    case _ =>
      parser
        .parse(new String(data, StandardCharsets.UTF_8))
        .valueOr(e => throw new SerializationException(e))
        .as[T]
        .valueOr(e => throw new SerializationException(e))
  }

  override def close(): Unit = {}
}
