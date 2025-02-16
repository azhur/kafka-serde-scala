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

package io.github.azhur.kafka.serde

import java.nio.charset.StandardCharsets.UTF_8
import java.util

import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import zio.json._

import scala.util.control.NonFatal

trait ZioJsonSupport {
  implicit def toSerializer[T >: Null](implicit encoder: JsonEncoder[T]): Serializer[T] =
    new Serializer[T] {
      override def configure(configs: util.Map[String, ?], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serialize(topic: String, data: T): Array[Byte] =
        if (data == null) null
        else
          try data.toJson.getBytes(UTF_8)
          catch {
            case NonFatal(e) => throw new SerializationException(e)
          }
    }

  implicit def toDeserializer[T >: Null](implicit decoder: JsonDecoder[T]): Deserializer[T] =
    new Deserializer[T] {
      override def configure(configs: util.Map[String, ?], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def deserialize(topic: String, data: Array[Byte]): T =
        if (data == null) null
        else
          decoder.decodeJson(new String(data, UTF_8)) match {
            case Left(value)  => throw new SerializationException(value)
            case Right(value) => value
          }
    }

  implicit def toSerde[T >: Null](implicit encoder: JsonEncoder[T], decoder: JsonDecoder[T]): Serde[T] =
    new Serde[T] {
      override def configure(configs: util.Map[String, ?], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serializer(): Serializer[T]                                   = toSerializer[T]
      override def deserializer(): Deserializer[T]                               = toDeserializer[T]
    }
}

object ZioJsonSupport extends ZioJsonSupport
