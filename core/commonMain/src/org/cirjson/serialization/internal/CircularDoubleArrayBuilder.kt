package org.cirjson.serialization.internal

@PublishedApi
internal class CircularDoubleArrayBuilder internal constructor(bufferWithData: DoubleArray) :
    CircularPrimitiveArrayBuilder<DoubleArray>() {

    private var buffer: DoubleArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: Double) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}