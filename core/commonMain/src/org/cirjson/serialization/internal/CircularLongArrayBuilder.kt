package org.cirjson.serialization.internal

@PublishedApi
internal class CircularLongArrayBuilder internal constructor(bufferWithData: LongArray) :
    CircularPrimitiveArrayBuilder<LongArray>() {

    private var buffer: LongArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: Long) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}