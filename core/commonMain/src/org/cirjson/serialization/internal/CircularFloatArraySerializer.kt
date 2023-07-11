package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularCompositeEncoder

/**
 * Serializer for [FloatArray].
 *
 * Encode elements one-by-one, as regular list,
 * unless format's Encoder/Decoder have special handling for this serializer.
 */
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal object CircularFloatArraySerializer : CircularKSerializer<FloatArray>,
    CircularPrimitiveArraySerializer<Float, FloatArray, CircularFloatArrayBuilder>(Float.serializer()) {

    override fun FloatArray.collectionSize(): Int = size

    override fun FloatArray.toBuilder(): CircularFloatArrayBuilder = CircularFloatArrayBuilder(this)

    override fun empty(): FloatArray = FloatArray(0)

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: CircularFloatArrayBuilder,
            checkIndex: Boolean) {
        builder.append(decoder.decodeFloatElement(descriptor, index))
    }

    override fun writeContent(encoder: CircularCompositeEncoder, content: FloatArray, size: Int) {
        for (i in 0..<size) encoder.encodeFloatElement(descriptor, i, content[i])
    }

}