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

package org.azhur.kafkaserdejson4s

import java.nio.charset.StandardCharsets
import java.util

import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import org.json4s.{ Formats, Serialization }

import scala.language.implicitConversions
import scala.util.control.NonFatal
import scala.util.{ Failure, Success, Try }

trait Json4sSupport {
  implicit def toSerializer[T <: AnyRef](implicit serialization: Serialization,
                                         formats: Formats): Serializer[T] = new Serializer[T] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
    override def close(): Unit                                                 = {}
    override def serialize(topic: String, data: T): Array[Byte] =
      Try(serialization.write[T](data).getBytes(StandardCharsets.UTF_8)) match {
        case Success(result)      => result
        case Failure(NonFatal(e)) => throw new SerializationException(e)
        case Failure(e)           => throw e
      }
  }

  implicit def toDeserializer[T <: AnyRef: Manifest](implicit serialization: Serialization,
                                                     formats: Formats): Deserializer[T] =
    new Deserializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def deserialize(topic: String, data: Array[Byte]): T =
        Try(serialization.read[T](new String(data, StandardCharsets.UTF_8))) match {
          case Success(result)      => result
          case Failure(NonFatal(e)) => throw new SerializationException(e)
          case Failure(e)           => throw e
        }
    }

  implicit def toSerde[T <: AnyRef: Manifest](implicit serialization: Serialization,
                                              formats: Formats): Serde[T] = new Serde[T] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
    override def close(): Unit                                                 = {}
    override def serializer(): Serializer[T]                                   = toSerializer[T]
    override def deserializer(): Deserializer[T]                               = toDeserializer[T]
  }
}

object Json4sSupport extends Json4sSupport
