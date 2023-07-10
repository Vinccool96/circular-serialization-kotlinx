package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularCompositeEncoder
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

/**
 * Serializer for [LongArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal object CircularULongArraySerializer : CircularKSerializer<ULongArray>,
    CircularPrimitiveArraySerializer<ULong, ULongArray, CircularULongArrayBuilder>(ULong.serializer()) {

    override fun ULongArray.collectionSize(): Int = size

    override fun ULongArray.toBuilder(): CircularULongArrayBuilder = CircularULongArrayBuilder(this)

    override fun empty(): ULongArray = ULongArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularULongArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeInlineElement(descriptor, index).decodeLong().toULong())
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: ULongArray, size: Int) {
        for (i in 0..<size) encoder.encodeInlineElement(descriptor, i).encodeLong(content[i].toLong())
    }

}