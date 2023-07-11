package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularCompositeEncoder

/**
 * Serializer for [DoubleArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal object CircularDoubleArraySerializer : CircularKSerializer<DoubleArray>,
    CircularPrimitiveArraySerializer<Double, DoubleArray, CircularDoubleArrayBuilder>(Double.serializer()) {

    override fun DoubleArray.collectionSize(): Int = size

    override fun DoubleArray.toBuilder(): CircularDoubleArrayBuilder = CircularDoubleArrayBuilder(this)

    override fun empty(): DoubleArray = DoubleArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularDoubleArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeDoubleElement(descriptor, index))
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: DoubleArray, size: Int) {
        for (i in 0..<size) encoder.encodeDoubleElement(descriptor, i, content[i])
    }

}