package org.cirjson.serialization.internal

/**
 * A class that combines the capabilities of ClassValue and SoftReference.
 * Softly binds the calculated value to the specified class.
 *
 * [SoftReference] used to prevent class loaders from leaking,
 * since the value can transitively refer to an instance of type [Class], this may prevent the loader from
 * being collected during garbage collection.
 *
 * In the first calculation the value is cached, every time [getOrSet] is called, a pre-calculated value is returned.
 *
 * However, the value can be collected during garbage collection (thanks to [SoftReference])
 * - in this case, when trying to call the [getOrSet] function, the value will be calculated again and placed in the cache.
 *
 * An important requirement for a function generating a value is that it must be stable, so that each time it is called for the same class, the function returns similar values.
 * In the case of serializers, these should be instances of the same class filled with equivalent values.
 */
@SuppressAnimalSniffer
internal class ClassValueReferences<T> : ClassValue<MutableSoftReference<T>>() {

    override fun computeValue(type: Class<*>): MutableSoftReference<T> {
        return MutableSoftReference()
    }

    inline fun getOrSet(key: Class<*>, crossinline factory: () -> T): T {
        val ref: MutableSoftReference<T> = get(key)

        ref.reference.get()?.let { return it }

        // go to the slow path and create serializer with blocking, also wrap factory block
        return ref.getOrSetWithLock { factory() }
    }

}