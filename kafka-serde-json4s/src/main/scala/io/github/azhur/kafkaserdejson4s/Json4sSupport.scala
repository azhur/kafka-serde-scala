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

package io.github.azhur.kafkaserdejson4s

import java.nio.charset.StandardCharsets.UTF_8
import java.util

import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import org.json4s.{ Formats, Serialization }

import scala.language.implicitConversions
import scala.util.control.NonFatal

trait Json4sSupport {
  implicit def json4sToSerializer[T <: AnyRef](implicit serialization: Serialization,
                                               formats: Formats): Serializer[T] =
    new Serializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serialize(topic: String, data: T): Array[Byte] =
        if (data == null) null
        else try serialization.write[T](data).getBytes(UTF_8) catch {
          case NonFatal(e) => throw new SerializationException(e)
        }
    }

  implicit def json4sToDeserializer[T >: Null <: AnyRef: Manifest](
      implicit serialization: Serialization,
      formats: Formats
  ): Deserializer[T] =
    new Deserializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def deserialize(topic: String, data: Array[Byte]): T =
        if (data == null) null
        else try serialization.read[T](new String(data, UTF_8)) catch {
          case NonFatal(e) => throw new SerializationException(e)
        }
    }

  implicit def json4sToSerde[T >: Null <: AnyRef: Manifest](implicit serialization: Serialization,
                                                            formats: Formats): Serde[T] =
    new Serde[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serializer(): Serializer[T]                                   = json4sToSerializer[T]
      override def deserializer(): Deserializer[T]                               = json4sToDeserializer[T]
    }
}

object Json4sSupport extends Json4sSupport
