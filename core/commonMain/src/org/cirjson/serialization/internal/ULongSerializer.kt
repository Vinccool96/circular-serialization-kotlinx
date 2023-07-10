package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object ULongSerializer : CircularKSerializer<ULong> {

    override val descriptor: CircularSerialDescriptor = InlinePrimitiveDescriptor("kotlin.ULong", Long.serializer())

    override fun serialize(encoder: CircularEncoder, value: ULong) {
        encoder.encodeInline(descriptor).encodeLong(value.toLong())
    }

    override fun deserialize(decoder: CircularDecoder): ULong {
        return decoder.decodeInline(descriptor).decodeLong().toULong()
    }

}