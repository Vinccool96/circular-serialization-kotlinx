package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class ClassValueParametrizedCache<T>(
        private val compute: (KClass<Any>, List<KType>) -> CircularKSerializer<T>?) :
    CircularParametrizedSerializerCache<T> {

    private val classValue = ClassValueReferences<ParametrizedCacheEntry<T>>()

    override fun get(key: KClass<Any>, types: List<KType>): Result<CircularKSerializer<T>?> {
        return classValue.getOrSet(key.java) { ParametrizedCacheEntry() }.computeIfAbsent(types) { compute(key, types) }
    }

}