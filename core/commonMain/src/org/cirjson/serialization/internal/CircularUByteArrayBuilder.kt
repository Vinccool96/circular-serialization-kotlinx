package org.cirjson.serialization.internal

@PublishedApi
@OptIn(ExperimentalUnsignedTypes::class)
internal class CircularUByteArrayBuilder internal constructor(bufferWithData: UByteArray) :
    CircularPrimitiveArrayBuilder<UByteArray>() {

    private var buffer: UByteArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: UByte) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}