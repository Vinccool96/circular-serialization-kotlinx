package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object BooleanSerializer : CircularKSerializer<Boolean> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.Boolean", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: CircularEncoder, value: Boolean): Unit = encoder.encodeBoolean(value)

    override fun deserialize(decoder: CircularDecoder): Boolean = decoder.decodeBoolean()

}