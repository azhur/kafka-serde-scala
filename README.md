# kafka-serde-scala

[![Join the chat at https://gitter.im/azhur/kafka-serde-scala](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/azhur/kafka-serde-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/azhur/kafka-serde-scala.svg?branch=master)](https://travis-ci.org/azhur/kafka-serde-scala) [![Join the chat at https://gitter.im/azhur/kafka-serde-scala](https://badges.gitter.im/azhur/kafka-serde-scala.svg)](https://gitter.im/azhur/kafka-serde-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

kafka-serde-scala provides implicit conversions from different type class Encoder/Decoder to kafka Serializer, Deserializer, Serde. 

Following target libraries are supported:
- [circe](https://circe.github.io/circe/)
- [Json4s](https://github.com/json4s/json4s)

Inspired by [https://github.com/hseeberger/akka-http-json](https://github.com/hseeberger/akka-http-json).

## Installation

``` scala
// All releases including intermediate ones are published here,
// final ones are also published to Maven Central.
resolvers += Resolver.bintrayRepo("azhur", "maven")

libraryDependencies ++= List(
  "io.github.azhur" %% "kafka-serde-scala" % "0.1.0",
  ...
)
```

## Usage

Mix `CirceSupport` into your code which requires implicit kafka `Serde|Serializer|Deserializer`, provide your implicit type class instances (i.e: io.circe.Encoder[T], io.circe.Decoder[T]) and the magic will convert them to kafka serializers.
For more info take a look at unit tests please.

## Contribution

Feel free to create PRs which will extend this library with new target encoders.

## License ##

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
