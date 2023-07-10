package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import kotlin.reflect.KClass

internal class ClassValueCache<T>(val compute: (KClass<*>) -> CircularKSerializer<T>?) : CircularSerializerCache<T> {

    private val classValue = ClassValueReferences<CacheEntry<T>>()

    override fun get(key: KClass<Any>): CircularKSerializer<T>? {
        return classValue.getOrSet(key.java) { CacheEntry(compute(key)) }.serializer
    }

}