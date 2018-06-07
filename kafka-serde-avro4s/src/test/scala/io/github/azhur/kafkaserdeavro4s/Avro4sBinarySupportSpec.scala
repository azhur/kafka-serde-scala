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

import com.sksamuel.avro4s.{ FromRecord, SchemaFor, ToRecord }
import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import org.scalatest.{ FreeSpec, Matchers }

object Avro4sBinarySupportSpec {
  case class Foo(a: Int, b: String, c: Boolean)

  def serializeFoo(foo: Foo)(implicit serializer: Serializer[Foo]): Array[Byte] =
    serializer.serialize("unused_topic", foo)

  def deserializeFoo(bytes: Array[Byte])(implicit deserializer: Deserializer[Foo]): Foo =
    deserializer.deserialize("unused_topic", bytes)

  def serdeFoo(bytes: Array[Byte])(implicit serde: Serde[Foo]): Foo =
    serde.deserializer().deserialize("unused_topic", bytes)

  implicit val schemaFor  = SchemaFor[Foo]
  implicit val toRecord   = ToRecord[Foo]
  implicit val fromRecord = FromRecord[Foo]
}

class Avro4sBinarySupportSpec extends FreeSpec with Matchers {
  import Avro4sBinarySupport._
  import Avro4sBinarySupportSpec._

  "Avro4sBinarySupport" - {
    "should implicitly convert to kafka Serializer" in {
      deserializeFoo(serializeFoo(Foo(1, "𝄞", false))) shouldBe Foo(1, "𝄞", false)
      serializeFoo(null) shouldBe null
    }
  }
}
