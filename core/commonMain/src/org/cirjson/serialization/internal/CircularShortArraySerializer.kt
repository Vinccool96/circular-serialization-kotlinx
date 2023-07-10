package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

/**
 * Serializer for [ShortArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal object CircularShortArraySerializer : CircularKSerializer<ShortArray>,
    CircularPrimitiveArraySerializer<Short, ShortArray, CircularShortArrayBuilder>(Short.serializer()) {

    override fun ShortArray.collectionSize(): Int = size

    override fun ShortArray.toBuilder(): CircularShortArrayBuilder = CircularShortArrayBuilder(this)

    override fun empty(): ShortArray = ShortArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularShortArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeShortElement(descriptor, index))
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: ShortArray, size: Int) {
        for (i in 0..<size) encoder.encodeShortElement(descriptor, i, content[i])
    }

}