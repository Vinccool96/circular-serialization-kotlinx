package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object StringSerializer : CircularKSerializer<String> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.String", PrimitiveKind.STRING)

    override fun serialize(encoder: CircularEncoder, value: String): Unit = encoder.encodeString(value)

    override fun deserialize(decoder: CircularDecoder): String = decoder.decodeString()

}