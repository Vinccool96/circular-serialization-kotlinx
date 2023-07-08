package org.cirjson.serialization.modules

import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.CircularSerializationStrategy

internal typealias PolymorphicCircularDeserializerProvider<Base> =
        (className: String?) -> CircularDeserializationStrategy<Base>?

internal typealias PolymorphicCircularSerializerProvider<Base> = (value: Base) -> CircularSerializationStrategy<Base>?
