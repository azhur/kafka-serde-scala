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

import scala.reflect.runtime.universe._

import com.fasterxml.jackson.core.FormatSchema
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import Jackson.typeReference

import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.control.NonFatal

trait JacksonFormatSchemaSupport {
  implicit def toSerializer[T <: AnyRef](implicit
    mapper: ObjectMapper,
    schema: FormatSchema
  ): Serializer[T] =
    new Serializer[T] {
      private val writer                                                         = mapper.writer(schema)
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serialize(topic: String, data: T): Array[Byte] =
        if (data == null) null
        else
          try writer.writeValueAsBytes(data)
          catch {
            case NonFatal(e) => throw new SerializationException(e)
          }
    }

  implicit def toDeserializer[T >: Null <: AnyRef](implicit
    mapper: ObjectMapper,
    schema: FormatSchema,
    tt: TypeTag[T]
  ): Deserializer[T] =
    new Deserializer[T] {
      private val reader = mapper.readerFor(typeReference[T]).`with`(schema)
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def deserialize(topic: String, data: Array[Byte]): T =
        if (data == null) null
        else
          try reader.readValue[T](data)
          catch {
            case NonFatal(e) => throw new SerializationException(e)
          }
    }

  implicit def toSerde[T >: Null <: AnyRef](implicit
    mapper: ObjectMapper,
    schema: FormatSchema,
    ct: TypeTag[T]
  ): Serde[T] =
    new Serde[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serializer(): Serializer[T]                                   = toSerializer[T]
      override def deserializer(): Deserializer[T]                               = toDeserializer[T]
    }
}

object JacksonFormatSchemaSupport extends JacksonFormatSchemaSupport
