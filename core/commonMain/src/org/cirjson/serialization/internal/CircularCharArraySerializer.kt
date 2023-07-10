package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

/**
 * Serializer for [CharArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal object CircularCharArraySerializer : CircularKSerializer<CharArray>,
    CircularPrimitiveArraySerializer<Char, CharArray, CircularCharArrayBuilder>(Char.serializer()) {

    override fun CharArray.collectionSize(): Int = size

    override fun CharArray.toBuilder(): CircularCharArrayBuilder = CircularCharArrayBuilder(this)

    override fun empty(): CharArray = CharArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularCharArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeCharElement(descriptor, index))
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: CharArray, size: Int) {
        for (i in 0..<size) encoder.encodeCharElement(descriptor, i, content[i])
    }

}