package org.cirjson.serialization.internal

import java.lang.ref.SoftReference

/**
 * Wrapper over `SoftReference`, used  to store a mutable value.
 */
internal class MutableSoftReference<T> {

    // volatile because of situations like https://stackoverflow.com/a/7855774
    @JvmField
    @Volatile
    var reference: SoftReference<T> = SoftReference(null)

    /*
    It is important that the monitor for synchronized is the `MutableSoftReference` of a specific class
    This way access to reference is blocked only for one serializable class, and not for all
     */
    @Synchronized
    fun getOrSetWithLock(factory: () -> T): T {
        // exit function if another thread has already filled in the `reference` with non-null value
        reference.get()?.let { return it }

        val value = factory()
        reference = SoftReference(value)
        return value
    }

}