package org.cirjson.serialization.internal

@PublishedApi
internal class CircularBooleanArrayBuilder internal constructor(bufferWithData: BooleanArray) :
    CircularPrimitiveArrayBuilder<BooleanArray>() {

    private var buffer: BooleanArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: Boolean) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}