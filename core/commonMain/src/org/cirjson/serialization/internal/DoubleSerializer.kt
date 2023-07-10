package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object DoubleSerializer : CircularKSerializer<Double> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.Double", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: CircularEncoder, value: Double): Unit = encoder.encodeDouble(value)

    override fun deserialize(decoder: CircularDecoder): Double = decoder.decodeDouble()

}