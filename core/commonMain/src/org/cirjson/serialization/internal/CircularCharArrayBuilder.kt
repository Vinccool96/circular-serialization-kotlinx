package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

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

    internal fun append(c: Byte) {
        ensureCapacity()
        buffer[position++] = c
    }

    override fun build() = buffer.copyOf(position)

}