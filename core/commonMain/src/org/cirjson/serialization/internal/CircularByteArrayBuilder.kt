package org.cirjson.serialization.internal

@PublishedApi
internal class CircularByteArrayBuilder internal constructor(bufferWithData: ByteArray) :
    CircularPrimitiveArrayBuilder<ByteArray>() {

    private var buffer: ByteArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: Byte) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}