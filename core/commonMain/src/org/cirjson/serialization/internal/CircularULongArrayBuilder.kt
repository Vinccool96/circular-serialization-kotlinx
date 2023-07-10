package org.cirjson.serialization.internal

@PublishedApi
@OptIn(ExperimentalUnsignedTypes::class)
internal class CircularULongArrayBuilder internal constructor(bufferWithData: ULongArray) :
    CircularPrimitiveArrayBuilder<ULongArray>() {

    private var buffer: ULongArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: ULong) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}