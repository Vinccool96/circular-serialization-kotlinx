package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object CharSerializer : CircularKSerializer<Char> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.Char", PrimitiveKind.CHAR)

    override fun serialize(encoder: CircularEncoder, value: Char): Unit = encoder.encodeChar(value)

    override fun deserialize(decoder: CircularDecoder): Char = decoder.decodeChar()

}