package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

/**
 * Serializer for [BooleanArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal object CircularBooleanArraySerializer : CircularKSerializer<BooleanArray>,
    CircularPrimitiveArraySerializer<Boolean, BooleanArray, CircularBooleanArrayBuilder>(Boolean.serializer()) {

    override fun BooleanArray.collectionSize(): Int = size

    override fun BooleanArray.toBuilder(): CircularBooleanArrayBuilder = CircularBooleanArrayBuilder(this)

    override fun empty(): BooleanArray = BooleanArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularBooleanArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeBooleanElement(descriptor, index))
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: BooleanArray, size: Int) {
        for (i in 0..<size) encoder.encodeBooleanElement(descriptor, i, content[i])
    }

}