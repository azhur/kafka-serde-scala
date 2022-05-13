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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class JacksonJsonSupportSpec extends AnyFreeSpec with Matchers {
  import JacksonJsonSupport._
  import ApiSpec._

  "JacksonJson" - {
    implicit val om: ObjectMapper = new ObjectMapper.registerModule(DefaultScalaModule)
    val foo                       = Foo(1, "𝄞")
    "should implicitly convert to kafka Serializer/Deserializer" in {
      serializeFoo(null) shouldBe null
      deserializeFoo(null) shouldBe null
      deserializeFoo(serializeFoo(foo)) shouldBe foo
    }

    "should implicitly convert to Serde" in {
      serdeFooSer(null) shouldBe null
      serdeFooDes(null) shouldBe null
      serdeFooDes(serdeFooSer(foo)) shouldBe foo
    }
  }
}
