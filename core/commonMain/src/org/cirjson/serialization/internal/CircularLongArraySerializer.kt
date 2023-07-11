package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularCompositeEncoder

/**
 * Serializer for [LongArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal object CircularLongArraySerializer : CircularKSerializer<LongArray>,
    CircularPrimitiveArraySerializer<Long, LongArray, CircularLongArrayBuilder>(Long.serializer()) {

    override fun LongArray.collectionSize(): Int = size

    override fun LongArray.toBuilder(): CircularLongArrayBuilder = CircularLongArrayBuilder(this)

    override fun empty(): LongArray = LongArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularLongArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeLongElement(descriptor, index))
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: LongArray, size: Int) {
        for (i in 0..<size) encoder.encodeLongElement(descriptor, i, content[i])
    }

}