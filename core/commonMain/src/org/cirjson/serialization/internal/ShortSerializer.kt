package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object ShortSerializer : CircularKSerializer<Short> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.Short", PrimitiveKind.SHORT)

    override fun serialize(encoder: CircularEncoder, value: Short): Unit = encoder.encodeShort(value)

    override fun deserialize(decoder: CircularDecoder): Short = decoder.decodeShort()

}