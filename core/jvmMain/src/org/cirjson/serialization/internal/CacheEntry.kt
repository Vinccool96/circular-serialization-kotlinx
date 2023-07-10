package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer

/**
 * Wrapper for cacheable serializer of some type.
 * Used to store cached serializer or indicates that the serializer is not cacheable.
 *
 * If serializer for type is not cacheable then value of [serializer] is `null`.
 */
internal class CacheEntry<T>(@JvmField val serializer: CircularKSerializer<T>?)