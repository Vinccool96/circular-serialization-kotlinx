package org.cirjson.serialization.builtins

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.ExperimentalCircularSerializationApi

/**
 * Returns a nullable serializer for the given serializer of non-null type.
 */
@OptIn(ExperimentalCircularSerializationApi::class)
public val <T : Any> CircularKSerializer<T>.nullable: CircularKSerializer<T?>
    get() {
        @Suppress("UNCHECKED_CAST")
        return if (descriptor.isNullable) (this as CircularKSerializer<T?>) else CircularNullableSerializer(this)
    }
