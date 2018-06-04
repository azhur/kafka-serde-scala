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

import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import org.json4s.{ DefaultFormats, jackson, native }
import org.scalatest.{ FreeSpec, Matchers }

object Json4sSupportSpec {
  case class Foo(a: Int, b: String)

  def serializeFoo(foo: Foo)(implicit serializer: Serializer[Foo]): Array[Byte] =
    serializer.serialize("unused_topic", foo)

  def deserializeFoo(bytes: Array[Byte])(implicit deserializer: Deserializer[Foo]): Foo =
    deserializer.deserialize("unused_topic", bytes)

  def serdeFoo(bytes: Array[Byte])(implicit serde: Serde[Foo]): Foo =
    serde.deserializer().deserialize("unused_topic", bytes)
}

class Json4sSupportSpec extends FreeSpec with Matchers {
  import Json4sSupportSpec._
  import io.github.azhur.kafkaserdejson4s.Json4sSupport._

  private implicit val formats = DefaultFormats

  "Json4sSupport jackson" - {
    implicit val serialization = jackson.Serialization
    "should implicitly convert to kafka Serializer" in {
      serializeFoo(Foo(1, "𝄞")) shouldBe """{"a":1,"b":"𝄞"}""".getBytes(UTF_8)
      serializeFoo(null) shouldBe null
    }

    "should implicitly convert to kafka Deserializer" in {
      deserializeFoo("""{"a":1,"b":"𝄞"}""".getBytes(UTF_8)) shouldBe Foo(1, "𝄞")
      deserializeFoo(null) shouldBe null
    }

    "should implicitly convert to Serde" in {
      serdeFoo("""{"a":1,"b":"𝄞"}""".getBytes(UTF_8)) shouldBe Foo(1, "𝄞")
    }
  }

  "Json4sSupport native" - {
    implicit val serialization = native.Serialization
    "should implicitly convert to kafka Serializer" in {
      serializeFoo(Foo(1, "𝄞")) shouldBe """{"a":1,"b":"𝄞"}""".getBytes(UTF_8)
      serializeFoo(null) shouldBe null
    }

    "should implicitly convert to kafka Deserializer" in {
      deserializeFoo("""{"a":1,"b":"𝄞"}""".getBytes(UTF_8)) shouldBe Foo(1, "𝄞")
      deserializeFoo(null) shouldBe null
    }

    "should implicitly convert to Serde" in {
      serdeFoo("""{"a":1,"b":"𝄞"}""".getBytes(UTF_8)) shouldBe Foo(1, "𝄞")
    }
  }
}
