package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import kotlin.reflect.KClass

/**
 * Cache class for non-parametrized and non-contextual serializers.
 */
internal interface CircularSerializerCache<T> {

    /**
     * Returns cached serializer or `null` if serializer not found.
     */
    fun get(key: KClass<Any>): CircularKSerializer<T>?

}