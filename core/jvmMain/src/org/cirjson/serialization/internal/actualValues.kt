package org.cirjson.serialization.internal

/*
 * By default, we use ClassValue-based caches to avoid classloader leaks,
 * but ClassValue is not available on Android, thus we attempt to check it dynamically
 * and fallback to ConcurrentHashMap-based cache.
 */
internal val useClassValue = try {
    Class.forName("java.lang.ClassValue")
    true
} catch (_: Throwable) {
    false
}
