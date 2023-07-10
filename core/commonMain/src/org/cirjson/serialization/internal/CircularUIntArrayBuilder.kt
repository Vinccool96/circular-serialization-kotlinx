package org.cirjson.serialization.internal

@PublishedApi
@OptIn(ExperimentalUnsignedTypes::class)
internal class CircularUIntArrayBuilder internal constructor(bufferWithData: UIntArray) :
    CircularPrimitiveArrayBuilder<UIntArray>() {

    private var buffer: UIntArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: UInt) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}