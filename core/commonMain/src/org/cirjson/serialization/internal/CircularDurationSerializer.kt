package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import kotlin.time.Duration

@PublishedApi
internal object CircularDurationSerializer : CircularKSerializer<Duration> {
    override val descriptor: CircularSerialDescriptor = PrimitiveSerialDescriptor("kotlin.time.Duration",
            PrimitiveKind.STRING)

    override fun serialize(encoder: CircularEncoder, value: Duration) {
        encoder.encodeString(value.toIsoString())
    }

    override fun deserialize(decoder: CircularDecoder): Duration {
        return Duration.parseIsoString(decoder.decodeString())
    }
}