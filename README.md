# kafka-serde-scala

[![Join the chat at https://gitter.im/azhur/kafka-serde-scala](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/azhur/kafka-serde-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/azhur/kafka-serde-scala.svg?branch=master)](https://travis-ci.org/azhur/kafka-serde-scala)
![Maven Central](https://img.shields.io/maven-central/v/io.github.azhur/kafka-serde-circe_2.12.svg)

kafka-serde-scala provides implicit conversions from different type class Encoder/Decoder to kafka Serializer, Deserializer, Serde. 

Following target libraries are supported:
- [avro4s](https://github.com/sksamuel/avro4s)
- [circe](https://circe.github.io/circe/)
- [jackson (json, protobuf, avro)](https://github.com/FasterXML/jackson)
- [json4s](https://github.com/json4s/json4s)
- [jsoniter-scala](https://github.com/plokhotnyuk/jsoniter-scala)
- [play-json](https://github.com/playframework/play-json)
- [upickle](https://github.com/lihaoyi/upickle)

Inspired by [https://github.com/hseeberger/akka-http-json](https://github.com/hseeberger/akka-http-json).

## Installation

Add dependencies for the selected integration:

- for avro4s:
``` scala
libraryDependencies ++= List(
  "io.github.azhur" %% "kafka-serde-avro4s" % "0.4.0",
)
```

- for circe:
``` scala
libraryDependencies ++= List(
  "io.github.azhur" %% "kafka-serde-circe" % "0.4.0",
)
```

- for jackson:
``` scala
libraryDependencies ++= List(
  "io.github.azhur" %% "kafka-serde-jackson" % "0.4.0",
)
```

- for json4s:
``` scala
libraryDependencies ++= List(
  "io.github.azhur" %% "kafka-serde-json4s" % "0.4.0",
)
```

- for jsoniter-scala:
``` scala
libraryDependencies ++= List(
  "io.github.azhur" %% "kafka-serde-jsoniter-scala" % "0.4.0",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "0.29.2" % Provided // required only in compile-time
)
```

- for play-json:
``` scala
libraryDependencies ++= List(
  "io.github.azhur" %% "kafka-serde-play-json" % "0.4.0"
)
```

- for upickle:
``` scala
libraryDependencies ++= List(
  "io.github.azhur" %% "kafka-serde-upickle" % "0.4.0"
)
```

## Usage

Mix `xxxSupport` into your code which requires implicit Kafka 
`Serde`, `Serializer` or `Deserializer`, where `xxx` is the target library used for serialization, i.e: CirceSupport.
 
Provide your implicit type class instances and the magic will convert them to Kafka serializers:
- for avro4s: `com.sksamuel.avro4s.SchemaFor[T]`, `com.sksamuel.avro4s.ToRecord[T]`, `com.sksamuel.avro4s.FromRecord[T]` 
- for circe: `io.circe.Encoder[T]`, `io.circe.Decoder[T]` 
- for jackson json: `com.fasterxml.jackson.databind.ObjectMapper`
- for jackson binary: `com.fasterxml.jackson.databind.ObjectMapper`, `org.codehaus.jackson.FormatSchema`
- for json4s: `org.json4s.DefaultFormats`, `org.json4s.Serialization`
- for jsoniter-scala: `com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec[T]`,  (and optionally 
`com.github.plokhotnyuk.jsoniter_scala.core.WriterConfig` or/and `com.github.plokhotnyuk.jsoniter_scala.core.ReaderConfig`)
- for play-json: `play.api.libs.json.Reads`, `play.api.libs.json.Writes`.  
- for upickle: `upickle.default.Reader`, `upickle.default.Writer`.  

For more info, please, take a look at unit tests and at `kafka-serde-scala-example` which is a kafka-streams (2.0) application with kafka-serde-scala usage.

## Contribution

Feel free to contribute with creating PR or opening issues.

## License ##

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
