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

package io.github.azhur.kafkaserdeplayjson

import java.nio.charset.StandardCharsets.UTF_8
import java.util

import io.github.azhur.kafkaserdeplayjson.PlayJsonSupport.PlayJsonError
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import play.api.libs.json.{ JsError, JsValue, Json, Reads, Writes }

import scala.util.control.NonFatal

trait PlayJsonSupport {
  implicit def toSerializer[T <: AnyRef](implicit
    writes: Writes[T],
    printer: JsValue => String = Json.stringify
  ): Serializer[T] =
    new Serializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serialize(topic: String, data: T): Array[Byte] =
        if (data == null) null
        else
          try printer(writes.writes(data)).getBytes(UTF_8)
          catch {
            case NonFatal(e) => throw new SerializationException(e)
          }
    }

  implicit def toDeserializer[T >: Null <: AnyRef: Manifest](implicit
    reads: Reads[T]
  ): Deserializer[T] =
    new Deserializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def deserialize(topic: String, data: Array[Byte]): T =
        if (data == null) null
        else
          reads
            .reads(Json.parse(new String(data, UTF_8)))
            .recoverTotal { e =>
              throw new SerializationException(PlayJsonError(e))
            }
    }

  implicit def toSerde[T >: Null <: AnyRef: Manifest](implicit
    writes: Writes[T],
    reads: Reads[T],
    printer: JsValue => String = Json.stringify
  ): Serde[T] =
    new Serde[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit                                                 = {}
      override def serializer(): Serializer[T]                                   = toSerializer[T]
      override def deserializer(): Deserializer[T]                               = toDeserializer[T]
    }
}

object PlayJsonSupport extends PlayJsonSupport {
  final case class PlayJsonError(error: JsError) extends RuntimeException {
    override def getMessage: String =
      JsError.toJson(error).toString()
  }
}
