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

package io.github.azhur.kafkaserdeavro4s

import java.io.ByteArrayOutputStream
import java.util

import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema, Decoder, Encoder, SchemaFor}
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

import scala.language.implicitConversions
import scala.util.{Failure, Success}
import scala.util.control.NonFatal

trait Avro4sBinarySupport {
  implicit def toSerializer[T >: Null: Encoder]: Serializer[T] =
    new Serializer[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit = {}
      override def serialize(topic: String, data: T): Array[Byte] =
        if (data == null) null
        else {
          val baos = new ByteArrayOutputStream()
          try {
            val output = AvroOutputStream.binary[T].to(baos).build()
            try output.write(data)
            finally output.close()
            baos.toByteArray
          } catch {
            case NonFatal(e) => throw new SerializationException(e)
          } finally baos.close()
        }
    }

  implicit def toDeserializer[T >: Null](implicit
    schemaFor: SchemaFor[T],
    decoder: Decoder[T]
  ): Deserializer[T] =
    new Deserializer[T] {
      private val schema = AvroSchema[T]
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit = {}
      override def deserialize(topic: String, data: Array[Byte]): T =
        if (data == null) null
        else {
          val it = AvroInputStream.binary[T].from(data).build(schema).tryIterator

          if (it.hasNext) {
            it.next() match {
              case Success(record) => record
              case Failure(err)    => throw new SerializationException(err)
            }
          } else {
            throw new SerializationException("Empty avro4s binary iterator")
          }
        }

    }

  implicit def toSerde[T >: Null: SchemaFor: Encoder: Decoder]: Serde[T] =
    new Serde[T] {
      override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}
      override def close(): Unit = {}
      override def serializer(): Serializer[T] = toSerializer[T]
      override def deserializer(): Deserializer[T] = toDeserializer[T]
    }
}

object Avro4sBinarySupport extends Avro4sBinarySupport
