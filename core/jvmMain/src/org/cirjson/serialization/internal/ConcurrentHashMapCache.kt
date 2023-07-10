package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * We no longer support Java 6, so the only place we use this cache is Android, where there
 * are no classloader leaks issue, thus we can safely use strong references and do not bother
 * with WeakReference wrapping.
 */
internal class ConcurrentHashMapCache<T>(private val compute: (KClass<*>) -> CircularKSerializer<T>?) :
    CircularSerializerCache<T> {

    private val cache = ConcurrentHashMap<Class<*>, CacheEntry<T>>()

    override fun get(key: KClass<Any>): CircularKSerializer<T>? {
        return cache.getOrPut(key.java) {
            CacheEntry(compute(key))
        }.serializer
    }

}