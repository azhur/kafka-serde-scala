/*
 * Copyright 2021 Artur Zhurat
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

package io.github.azhur.kafkaserdescalapb

import org.apache.kafka.common.serialization.Serializer
import scalapb.GeneratedMessageCompanion
import scalapb.GeneratedMessage
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde

trait ScalaPbSupport {
  implicit def toSerializer[T <: GeneratedMessage]: Serializer[T] = new Serializer[T] {
    override def serialize(topic: String, data: T): Array[Byte] = if (data == null) null else data.toByteArray
  }

  implicit def toDeserializer[T >: Null <: GeneratedMessage](implicit
    companion: GeneratedMessageCompanion[T]
  ): Deserializer[T] = new Deserializer[T] {
    override def deserialize(topic: String, data: Array[Byte]): T =
      if (data == null) null else companion.parseFrom(data)

  }

  implicit def toSerde[T >: Null <: GeneratedMessage: GeneratedMessageCompanion]: Serde[T] =
    new Serde[T] {
      override def serializer(): Serializer[T]     = toSerializer[T]
      override def deserializer(): Deserializer[T] = toDeserializer[T]
    }
}

object ScalaPbSupport extends ScalaPbSupport
