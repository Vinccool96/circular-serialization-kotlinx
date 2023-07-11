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
@OptIn(InternalCircularSerializationApi::class)
internal object CircularByteArraySerializer : CircularKSerializer<ByteArray>,
    CircularPrimitiveArraySerializer<Byte, ByteArray, CircularByteArrayBuilder>(Byte.serializer()) {

    override fun ByteArray.collectionSize(): Int = size

    override fun ByteArray.toBuilder(): CircularByteArrayBuilder = CircularByteArrayBuilder(this)

    override fun empty(): ByteArray = ByteArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularByteArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeByteElement(descriptor, index))
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: ByteArray, size: Int) {
        for (i in 0..<size) encoder.encodeByteElement(descriptor, i, content[i])
    }

}