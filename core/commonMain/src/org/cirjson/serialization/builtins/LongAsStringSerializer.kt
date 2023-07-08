package org.cirjson.serialization.builtins

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

/**
 * Serializer that encodes and decodes [Long] as its string representation.
 *
 * Intended to be used for interoperability with external clients (mainly JavaScript ones),
 * where numbers can't be parsed correctly if they exceed
 * [`abs(2^53-1)`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/MAX_SAFE_INTEGER).
 */
public object LongAsStringSerializer : CircularKSerializer<Long> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlinx.serialization.LongAsStringSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Long) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Long {
        return decoder.decodeString().toLong()
    }

}