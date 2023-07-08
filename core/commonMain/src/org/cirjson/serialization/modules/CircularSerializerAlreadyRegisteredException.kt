package org.cirjson.serialization.modules

import kotlin.reflect.KClass

internal class CircularSerializerAlreadyRegisteredException internal constructor(msg: String) :
        IllegalArgumentException(msg) {

    internal constructor(baseClass: KClass<*>, concreteClass: KClass<*>) : this(
            "Serializer for $concreteClass already registered in the scope of $baseClass")

}