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
 * Serializer for [IntArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class, ExperimentalUnsignedTypes::class)
internal object CircularUIntArraySerializer : CircularKSerializer<UIntArray>,
    CircularPrimitiveArraySerializer<UInt, UIntArray, CircularUIntArrayBuilder>(UInt.serializer()) {

    override fun UIntArray.collectionSize(): Int = size

    override fun UIntArray.toBuilder(): CircularUIntArrayBuilder = CircularUIntArrayBuilder(this)

    override fun empty(): UIntArray = UIntArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularUIntArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeInlineElement(descriptor, index).decodeInt().toUInt())
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: UIntArray, size: Int) {
        for (i in 0..<size) encoder.encodeInlineElement(descriptor, i).encodeInt(content[i].toInt())
    }

}