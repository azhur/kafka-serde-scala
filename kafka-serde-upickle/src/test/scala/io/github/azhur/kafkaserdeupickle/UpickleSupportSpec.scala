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

package io.github.azhur.kafkaserdeupickle

import java.nio.charset.StandardCharsets.UTF_8

import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import org.scalatest.{ FreeSpec, Matchers }
import upickle.default.{ ReadWriter, macroRW }

object UpickleSupportSpec {
  case class Foo(a: Int, b: String)

  def serializeFoo(foo: Foo)(implicit serializer: Serializer[Foo]): Array[Byte] =
    serializer.serialize("unused_topic", foo)

  def deserializeFoo(bytes: Array[Byte])(implicit deserializer: Deserializer[Foo]): Foo =
    deserializer.deserialize("unused_topic", bytes)

  def serdeFooDes(bytes: Array[Byte])(implicit serde: Serde[Foo]): Foo =
    serde.deserializer().deserialize("unused_topic", bytes)

  def serdeFooSer(foo: Foo)(implicit serde: Serde[Foo]): Array[Byte] =
    serde.serializer().serialize("unused_topic", foo)

  implicit val rw: ReadWriter[Foo] = macroRW
}

class UpickleSupportSpec extends FreeSpec with Matchers {
  import UpickleSupport._
  import UpickleSupportSpec._

  "UpickleSupport" - {
    "should implicitly convert to kafka Serializer" in {
      // upickle serializes as {"a":1,"b":"\ud834\udd1e"}
      deserializeFoo(serializeFoo(Foo(1, "𝄞"))) shouldBe Foo(1, "𝄞")
      serializeFoo(null) shouldBe null
    }

    "should implicitly convert to kafka Deserializer" in {
      deserializeFoo("""{"a":1,"b":"𝄞"}""".getBytes(UTF_8)) shouldBe Foo(1, "𝄞")
      deserializeFoo(null) shouldBe null
    }

    "should implicitly convert to kafka Serde" in {
      val foo           = Foo(1, "𝄞")
      val serializedFoo = """{"a":1,"b":"𝄞"}""".getBytes(UTF_8)

      serdeFooDes(serializedFoo) shouldBe foo
      serdeFooDes(null) shouldBe null

      serdeFooSer(foo) shouldBe serializedFoo
      serdeFooSer(null) shouldBe null
    }
  }
}
