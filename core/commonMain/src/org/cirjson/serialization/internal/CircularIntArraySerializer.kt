package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

/**
 * Serializer for [IntArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal object CircularIntArraySerializer : CircularKSerializer<IntArray>,
    CircularPrimitiveArraySerializer<Int, IntArray, CircularIntArrayBuilder>(Int.serializer()) {

    override fun IntArray.collectionSize(): Int = size

    override fun IntArray.toBuilder(): CircularIntArrayBuilder = CircularIntArrayBuilder(this)

    override fun empty(): IntArray = IntArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularIntArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeIntElement(descriptor, index))
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: IntArray, size: Int) {
        for (i in 0..<size) encoder.encodeIntElement(descriptor, i, content[i])
    }

}