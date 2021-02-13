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

import org.apache.kafka.common.serialization.{ Deserializer, Serde, Serializer }
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import io.github.azhur.scalaserdescalapb.proto.SearchRequest.SearchRequest
import scalapb.GeneratedMessageCompanion

object ScalaPbSupportSpec {
  def serializeSearchRequest(req: SearchRequest)(implicit serializer: Serializer[SearchRequest]): Array[Byte] =
    serializer.serialize("unused_topic", req)

  def deserializeSearchRequest(bytes: Array[Byte])(implicit deserializer: Deserializer[SearchRequest]): SearchRequest =
    deserializer.deserialize("unused_topic", bytes)

  def serdeSearchRequestDes(bytes: Array[Byte])(implicit serde: Serde[SearchRequest]): SearchRequest =
    serde.deserializer().deserialize("unused_topic", bytes)

  def serdeSearchRequestSer(foo: SearchRequest)(implicit serde: Serde[SearchRequest]): Array[Byte] =
    serde.serializer().serialize("unused_topic", foo)

  implicit val des: GeneratedMessageCompanion[SearchRequest] = SearchRequest
}

class ScalaPbSupportSpec extends AnyFreeSpec with Matchers {
  "ScalaPbSupport" - {
    "should implicitly convert to kafka Serializer/Deserializer/Serde" in {
      import ScalaPbSupportSpec._
      import ScalaPbSupport._

      val req = SearchRequest(query = "q", pageNumber = Some(1), resultPerPage = Some(2))

      val serializedReq = serializeSearchRequest(req)
      deserializeSearchRequest(serializedReq) shouldBe req
      deserializeSearchRequest(serializeSearchRequest(null)) shouldBe null

      serdeSearchRequestDes(serializedReq) shouldBe req
      serdeSearchRequestSer(req) shouldBe serializedReq
    }
  }
}
