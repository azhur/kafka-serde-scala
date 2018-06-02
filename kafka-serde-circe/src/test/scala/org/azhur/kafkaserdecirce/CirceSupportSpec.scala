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

package org.azhur.kafkaserdecirce

import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import org.scalatest.{ FreeSpec, Matchers }

object CirceSupportSpec {
  case class Foo(a: Int, b: String)

  def serializeFoo(foo: Foo)(implicit serializer: Serializer[Foo]): Array[Byte] =
    serializer.serialize("unused_topic", foo)

  def deserializeFoo(bytes: Array[Byte])(implicit deserializer: Deserializer[Foo]): Foo =
    deserializer.deserialize("unused_topic", bytes)

  def serdeFoo(bytes: Array[Byte])(implicit serde: Serde[Foo]): Foo =
    serde.deserializer().deserialize("unused_topic", bytes)
}

class CirceSupportSpec extends FreeSpec with Matchers {
  import io.circe.generic.auto._
  import CirceSupportSpec._
  import CirceSupport._

  "CirceSupport" - {
    "should implicitly convert circe Encoder to kafka Serializer" in {
      serializeFoo(Foo(1, "2")) shouldBe """{"a":1,"b":"2"}""".getBytes
    }

    "should implicitly convert circe Decoder to kafka Deserializer" in {
      deserializeFoo("""{"a":1,"b":"2"}""".getBytes) shouldBe Foo(1, "2")
    }

    "should implicitly convert circe Encoder and Decoder to kafka Serde" in {
      serdeFoo("""{"a":1,"b":"2"}""".getBytes) shouldBe Foo(1, "2")
    }
  }
}
