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

package io.github.azhur.kafkaserdejackson

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import Jackson.typeReference
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

import scala.language.implicitConversions
import scala.reflect.runtime.universe._
import scala.util.control.NonFatal

trait JacksonJsonSupport {
  implicit def toSerializer[T <: AnyRef](implicit mapper: ObjectMapper): Serializer[T] =
    new Serializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit = {}
      override def serialize(topic: String, data: T): Array[Byte] =
        if (data == null) null
        else
          try mapper.writeValueAsBytes(data)
          catch {
            case NonFatal(e) => throw new SerializationException(e)
          }
    }

  implicit def toDeserializer[T >: Null <: AnyRef](implicit
    mapper: ObjectMapper,
    tt: TypeTag[T]
  ): Deserializer[T] =
    new Deserializer[T] {
      private val tr = typeReference[T]
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit = {}
      override def deserialize(topic: String, data: Array[Byte]): T =
        if (data == null) null
        else
          try mapper.readValue[T](data, tr)
          catch {
            case NonFatal(e) => throw new SerializationException(e)
          }
    }

  implicit def toSerde[T >: Null <: AnyRef](implicit
    mapper: ObjectMapper,
    tt: TypeTag[T]
  ): Serde[T] =
    new Serde[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit = {}
      override def serializer(): Serializer[T] = toSerializer[T]
      override def deserializer(): Deserializer[T] = toDeserializer[T]
    }
}

object JacksonJsonSupport extends JacksonJsonSupport
