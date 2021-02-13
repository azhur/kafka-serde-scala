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

import com.sksamuel.avro4s.{Decoder, Encoder, SchemaFor}
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

object Avro4sBinarySupportSpec {
  case class Foo(a: Int, b: String, c: Boolean)

  def serializeFoo(foo: Foo)(implicit serializer: Serializer[Foo]): Array[Byte] =
    serializer.serialize("unused_topic", foo)

  def deserializeFoo(bytes: Array[Byte])(implicit deserializer: Deserializer[Foo]): Foo =
    deserializer.deserialize("unused_topic", bytes)

  def serdeFooDes(bytes: Array[Byte])(implicit serde: Serde[Foo]): Foo =
    serde.deserializer().deserialize("unused_topic", bytes)

  def serdeFooSer(foo: Foo)(implicit serde: Serde[Foo]): Array[Byte] =
    serde.serializer().serialize("unused_topic", foo)

  implicit val schemaFor = SchemaFor[Foo]
  implicit val encoder = Encoder[Foo]
  implicit val decoder = Decoder[Foo]
}

class Avro4sBinarySupportSpec extends AnyFreeSpec with Matchers {
  import Avro4sBinarySupport._
  import Avro4sBinarySupportSpec._

  "Avro4sBinarySupport" - {
    "should implicitly convert to kafka Serializer/Deserializer/Serde" in {
      val schema = schemaFor.schema.toString
      val foo = Foo(1, "𝄞", false)

      val serializedFoo = serializeFoo(foo)
      new String(serializedFoo) shouldNot include(schema)

      deserializeFoo(serializedFoo) shouldBe foo
      serializeFoo(deserializeFoo(null)) shouldBe null

      serdeFooDes(serializedFoo) shouldBe foo
      serdeFooDes(null) shouldBe null

      new String(serdeFooSer(foo)) shouldNot include(schema)
      serdeFooSer(null) shouldBe null
    }
  }
}
