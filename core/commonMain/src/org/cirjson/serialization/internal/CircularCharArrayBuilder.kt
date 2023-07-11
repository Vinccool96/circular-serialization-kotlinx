package org.cirjson.serialization.internal

@PublishedApi
internal class CircularCharArrayBuilder internal constructor(bufferWithData: CharArray) :
    CircularPrimitiveArrayBuilder<CharArray>() {

    private var buffer: CharArray = bufferWithData

    override var position: Int = bufferWithData.size
        private set

    init {
        ensureCapacity(INITIAL_SIZE)
    }

    override fun ensureCapacity(requiredCapacity: Int) {
        if (buffer.size < requiredCapacity) buffer = buffer.copyOf(requiredCapacity.coerceAtLeast(buffer.size * 2))
    }

    internal fun append(c: Char) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}