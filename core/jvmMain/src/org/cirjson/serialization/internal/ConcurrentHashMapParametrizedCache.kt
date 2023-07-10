package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class ConcurrentHashMapParametrizedCache<T>(
        private val compute: (KClass<Any>, List<KType>) -> CircularKSerializer<T>?) :
    CircularParametrizedSerializerCache<T> {

    private val cache = ConcurrentHashMap<Class<*>, ParametrizedCacheEntry<T>>()

    override fun get(key: KClass<Any>, types: List<KType>): Result<CircularKSerializer<T>?> {
        return cache.getOrPut(key.java) { ParametrizedCacheEntry() }.computeIfAbsent(types) { compute(key, types) }
    }

}