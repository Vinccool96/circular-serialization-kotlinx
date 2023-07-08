package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object CircularNothingSerializer : CircularKSerializer<Nothing> {
    override val descriptor: CircularSerialDescriptor = CircularNothingSerialDescriptor

    override fun serialize(encoder: CircularEncoder, value: Nothing) {
        throw CircularSerializationException("'kotlin.Nothing' cannot be serialized")
    }

    override fun deserialize(decoder: CircularDecoder): Nothing {
        throw CircularSerializationException("'kotlin.Nothing' does not have instances")
    }
}