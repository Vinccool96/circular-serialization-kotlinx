package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularCompositeEncoder

/**
 * Serializer for [ByteArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class, ExperimentalUnsignedTypes::class)
internal object CircularUByteArraySerializer : CircularKSerializer<UByteArray>,
    CircularPrimitiveArraySerializer<UByte, UByteArray, CircularUByteArrayBuilder>(UByte.serializer()) {

    override fun UByteArray.collectionSize(): Int = size

    override fun UByteArray.toBuilder(): CircularUByteArrayBuilder = CircularUByteArrayBuilder(this)

    override fun empty(): UByteArray = UByteArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularUByteArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeInlineElement(descriptor, index).decodeByte().toUByte())
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: UByteArray, size: Int) {
        for (i in 0..<size) encoder.encodeInlineElement(descriptor, i).encodeByte(content[i].toByte())
    }

}