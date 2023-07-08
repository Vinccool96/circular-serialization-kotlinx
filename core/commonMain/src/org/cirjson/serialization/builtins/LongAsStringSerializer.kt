package org.cirjson.serialization.builtins

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.descriptors.PrimitiveSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

/**
 * Serializer that encodes and decodes [Long] as its string representation.
 *
 * Intended to be used for interoperability with external clients (mainly JavaScript ones),
 * where numbers can't be parsed correctly if they exceed
 * [`abs(2^53-1)`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/MAX_SAFE_INTEGER).
 */
public object LongAsStringSerializer : CircularKSerializer<Long> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("org.cirjson.serialization.LongAsStringSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: CircularEncoder, value: Long) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: CircularDecoder): Long {
        return decoder.decodeString().toLong()
    }

}