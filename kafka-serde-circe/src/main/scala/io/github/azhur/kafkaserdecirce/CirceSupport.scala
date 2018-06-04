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

package io.github.azhur.kafkaserdecirce

import java.nio.charset.StandardCharsets.UTF_8
import java.util

import io.circe.{ Decoder, Encoder }
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }

import scala.language.implicitConversions
import scala.util.control.NonFatal

trait CirceSupport {
  implicit def circeToSerializer[T >: Null](implicit encoder: Encoder[T]): Serializer[T] =
    new Serializer[T] {
      import io.circe.syntax._
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serialize(topic: String, data: T): Array[Byte] =
        if (data == null) null
        else try data.asJson.noSpaces.getBytes(UTF_8) catch {
          case NonFatal(e) => throw new SerializationException(e)
        }
    }

  implicit def circeToDeserializer[T >: Null](implicit decoder: Decoder[T]): Deserializer[T] =
    new Deserializer[T] {
      import io.circe._
      import cats.syntax.either._

      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def deserialize(topic: String, data: Array[Byte]): T =
        if (data == null) null
        else parser
          .parse(new String(data, UTF_8))
          .valueOr(e => throw new SerializationException(e))
          .as[T]
          .valueOr(e => throw new SerializationException(e))
    }

  implicit def circeToSerde[T >: Null](implicit encoder: Encoder[T],
                                       decoder: Decoder[T]): Serde[T] =
    new Serde[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serializer(): Serializer[T]                                   = circeToSerializer[T]
      override def deserializer(): Deserializer[T]                               = circeToDeserializer[T]
    }
}

object CirceSupport extends CirceSupport
