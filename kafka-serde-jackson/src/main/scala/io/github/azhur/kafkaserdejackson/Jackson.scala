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

import com.fasterxml.jackson.core.`type`.TypeReference
import scala.reflect.runtime.universe._
import java.lang.reflect.{ ParameterizedType, Type => JType }

object Jackson {
  def typeReference[T: TypeTag]: TypeReference[T] = {
    val t = typeTag[T]
    val mirror = t.mirror
    def mapType(t: Type): JType =
      if (t.typeArgs.isEmpty) {
        mirror.runtimeClass(t)
      } else {
        new ParameterizedType {
          def getRawType: JType = mirror.runtimeClass(t)
          def getActualTypeArguments: Array[JType] = t.typeArgs.map(mapType).toArray
          def getOwnerType: JType = null
        }
      }

    new TypeReference[T] {
      override def getType: JType = mapType(t.tpe)
    }
  }
}
