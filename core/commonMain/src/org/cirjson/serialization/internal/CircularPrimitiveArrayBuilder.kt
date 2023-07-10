package org.cirjson.serialization.internal

@PublishedApi
internal abstract class CircularPrimitiveArrayBuilder<Array> internal constructor() {

    internal abstract val position: Int

    internal abstract fun ensureCapacity(requiredCapacity: Int = position + 1)

    internal abstract fun build(): Array

}