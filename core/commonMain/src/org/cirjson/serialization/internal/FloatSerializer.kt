package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object FloatSerializer : CircularKSerializer<Float> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.Float", PrimitiveKind.FLOAT)

    override fun serialize(encoder: CircularEncoder, value: Float): Unit = encoder.encodeFloat(value)

    override fun deserialize(decoder: CircularDecoder): Float = decoder.decodeFloat()

}