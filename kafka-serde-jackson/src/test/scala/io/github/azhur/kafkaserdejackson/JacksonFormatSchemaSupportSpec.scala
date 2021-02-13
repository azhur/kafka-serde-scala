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

import com.fasterxml.jackson.dataformat.avro.{ AvroMapper, AvroSchema }
import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class JacksonFormatSchemaSupportSpec extends AnyFreeSpec with Matchers {
  import JacksonFormatSchemaSupport._
  import ApiSpec._

  "JacksonAvro" - {
    implicit val om: AvroMapper =
      new AvroMapper().registerModule(DefaultScalaModule).asInstanceOf[AvroMapper]
    implicit val schema: AvroSchema = om.schemaFor(classOf[Foo])
    val foo                         = Foo(1, "𝄞")
    val serializedFoo: Array[Byte]  = Array(2, 2, 8, -16, -99, -124, -98)
    "should implicitly convert to kafka Serializer" in {
      serializeFoo(null) shouldBe null
      serializeFoo(foo) shouldBe serializedFoo
    }

    "should implicitly convert to kafka Deserializer" in {
      deserializeFoo(serializedFoo) shouldBe foo
      deserializeFoo(null) shouldBe null
    }

    "should implicitly convert to Serde" in {
      serdeFooSer(null) shouldBe null
      serdeFooSer(foo) shouldBe serializedFoo

      serdeFooDes(null) shouldBe null
      serdeFooDes(serializedFoo) shouldBe foo
    }
  }

  "JacksonProtobuf" - {
    implicit val om: ProtobufMapper =
      new ProtobufMapper().registerModule(DefaultScalaModule).asInstanceOf[ProtobufMapper]
    implicit val schema: ProtobufSchema = om.generateSchemaFor(classOf[Foo])
    val foo                             = Foo(1, "𝄞")
    val serializedFoo: Array[Byte]      = Array(8, 1, 18, 4, -16, -99, -124, -98)
    "should implicitly convert to kafka Serializer" in {
      serializeFoo(null) shouldBe null
      serializeFoo(foo) shouldBe serializedFoo
    }

    "should implicitly convert to kafka Deserializer" in {
      deserializeFoo(serializedFoo) shouldBe foo
      deserializeFoo(null) shouldBe null
    }

    "should implicitly convert to Serde" in {
      serdeFooSer(null) shouldBe null
      serdeFooSer(foo) shouldBe serializedFoo

      serdeFooDes(null) shouldBe null
      serdeFooDes(serializedFoo) shouldBe foo
    }
  }

}
