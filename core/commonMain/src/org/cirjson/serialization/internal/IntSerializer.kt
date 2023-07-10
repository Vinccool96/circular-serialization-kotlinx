package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object IntSerializer : CircularKSerializer<Int> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.Int", PrimitiveKind.INT)

    override fun serialize(encoder: CircularEncoder, value: Int): Unit = encoder.encodeInt(value)

    override fun deserialize(decoder: CircularDecoder): Int = decoder.decodeInt()

}