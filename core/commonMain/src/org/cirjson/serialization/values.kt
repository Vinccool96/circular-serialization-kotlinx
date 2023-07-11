package org.cirjson.serialization

import org.cirjson.serialization.builtins.nullable
import org.cirjson.serialization.internal.cast
import org.cirjson.serialization.internal.createCache
import org.cirjson.serialization.internal.createParametrizedCache
import org.cirjson.serialization.modules.EmptyCircularSerializersModule
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Cache for non-null non-parametrized and non-contextual serializers.
 */
@ThreadLocal
@OptIn(InternalCircularSerializationApi::class)
private val SERIALIZERS_CACHE = createCache { it.serializerOrNull() }

/**
 * Cache for nullable non-parametrized and non-contextual serializers.
 */
@ThreadLocal
@OptIn(InternalCircularSerializationApi::class)
private val SERIALIZERS_CACHE_NULLABLE = createCache<Any?> { it.serializerOrNull()?.nullable?.cast() }

/**
 * Cache for non-null parametrized and non-contextual serializers.
 */
@ThreadLocal
private val PARAMETRIZED_SERIALIZERS_CACHE = createParametrizedCache { clazz, types ->
    val serializers = EmptyCircularSerializersModule().serializersForParameters(types, true)!!
    clazz.parametrizedSerializerOrNull(serializers) { types[0].classifier }
}

/**
 * Cache for nullable parametrized and non-contextual serializers.
 */
@ThreadLocal
private val PARAMETRIZED_SERIALIZERS_CACHE_NULLABLE = createParametrizedCache<Any?> { clazz, types ->
    val serializers = EmptyCircularSerializersModule().serializersForParameters(types, true)!!
    clazz.parametrizedSerializerOrNull(serializers) { types[0].classifier }?.nullable?.cast()
}

/**
 * Find cacheable serializer in the cache.
 * If serializer is cacheable but missed in cache - it will be created, placed into the cache and returned.
 */
internal fun findCachedSerializer(clazz: KClass<Any>, isNullable: Boolean): CircularKSerializer<Any?>? {
    return if (!isNullable) {
        SERIALIZERS_CACHE.get(clazz)?.cast()
    } else {
        SERIALIZERS_CACHE_NULLABLE.get(clazz)
    }
}

/**
 * Find cacheable parametrized serializer in the cache.
 * If serializer is cacheable but missed in cache - it will be created, placed into the cache and returned.
 */
internal fun findParametrizedCachedSerializer(
        clazz: KClass<Any>,
        types: List<KType>,
        isNullable: Boolean
): Result<CircularKSerializer<Any?>?> {
    return if (!isNullable) {
        @Suppress("UNCHECKED_CAST")
        PARAMETRIZED_SERIALIZERS_CACHE.get(clazz, types) as Result<CircularKSerializer<Any?>?>
    } else {
        PARAMETRIZED_SERIALIZERS_CACHE_NULLABLE.get(clazz, types)
    }
}
