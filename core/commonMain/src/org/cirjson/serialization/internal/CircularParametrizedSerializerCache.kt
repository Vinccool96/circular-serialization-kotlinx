package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Cache class for parametrized and non-contextual serializers.
 */
internal interface CircularParametrizedSerializerCache<T> {

    /**
     * Returns successful result with cached serializer or `null` if root serializer not found.
     * If no serializer was found for the parameters, then result contains an exception.
     */
    fun get(key: KClass<Any>, types: List<KType> = emptyList()): Result<CircularKSerializer<T>?>

}