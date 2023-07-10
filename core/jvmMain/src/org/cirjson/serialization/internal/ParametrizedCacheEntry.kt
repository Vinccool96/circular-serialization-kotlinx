package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KType

internal class ParametrizedCacheEntry<T> {

    private val serializers: ConcurrentHashMap<List<KTypeWrapper>, Result<CircularKSerializer<T>?>> =
            ConcurrentHashMap()

    inline fun computeIfAbsent(types: List<KType>,
            producer: () -> CircularKSerializer<T>?): Result<CircularKSerializer<T>?> {
        val wrappedTypes = types.map { KTypeWrapper(it) }
        return serializers.getOrPut(wrappedTypes) {
            kotlin.runCatching { producer() }
        }
    }

}