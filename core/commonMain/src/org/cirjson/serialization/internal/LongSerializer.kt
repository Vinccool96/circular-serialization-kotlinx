package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object LongSerializer : CircularKSerializer<Long> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.Long", PrimitiveKind.LONG)

    override fun serialize(encoder: CircularEncoder, value: Long): Unit = encoder.encodeLong(value)

    override fun deserialize(decoder: CircularDecoder): Long = decoder.decodeLong()

}