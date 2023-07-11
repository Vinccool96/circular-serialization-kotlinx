package org.cirjson.serialization.internal

@PublishedApi
@OptIn(ExperimentalUnsignedTypes::class)
internal class CircularUShortArrayBuilder internal constructor(bufferWithData: UShortArray) :
    CircularPrimitiveArrayBuilder<UShortArray>() {

    private var buffer: UShortArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: UShort) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}