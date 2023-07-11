package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularCompositeEncoder

/**
 * Serializer for [ShortArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class, ExperimentalUnsignedTypes::class)
internal object CircularUShortArraySerializer : CircularKSerializer<UShortArray>,
    CircularPrimitiveArraySerializer<UShort, UShortArray, CircularUShortArrayBuilder>(UShort.serializer()) {

    override fun UShortArray.collectionSize(): Int = size

    override fun UShortArray.toBuilder(): CircularUShortArrayBuilder = CircularUShortArrayBuilder(this)

    override fun empty(): UShortArray = UShortArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularUShortArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeInlineElement(descriptor, index).decodeShort().toUShort())
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: UShortArray, size: Int) {
        for (i in 0..<size) encoder.encodeInlineElement(descriptor, i).encodeShort(content[i].toShort())
    }

}