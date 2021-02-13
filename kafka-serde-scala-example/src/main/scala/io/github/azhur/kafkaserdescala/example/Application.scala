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

package io.github.azhur.kafkaserdescala.example
import java.util.Properties

import io.github.azhur.kafkaserdecirce.CirceSupport
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.{ KafkaStreams, StreamsConfig, Topology }
import org.apache.kafka.streams.scala.StreamsBuilder

object Application extends App with CirceSupport {
  import io.circe.generic.auto._
  import org.apache.kafka.streams.scala.serialization.Serdes._
  import org.apache.kafka.streams.scala.ImplicitConversions._

  case class User(id: Long, name: String, age: Int)

  val topology = buildTopology("input_users", "output_users")

  val streamingApp = new KafkaStreams(topology, streamProperties())
  streamingApp.start()

  sys.addShutdownHook {
    streamingApp.close()
  }

  def buildTopology(inputTopic: String, outputTopic: String): Topology = {
    val streamsBuilder = new StreamsBuilder()
    streamsBuilder
      .stream[String, User](inputTopic)
      .filter((_, user) => user.age > 18)
      .to(outputTopic)

    streamsBuilder.build()
  }

  def streamProperties(): Properties = {
    val streamsConfiguration = new Properties
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-app")
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    streamsConfiguration.put(
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
      Topology.AutoOffsetReset.EARLIEST.toString.toLowerCase
    )
    streamsConfiguration
  }
}
