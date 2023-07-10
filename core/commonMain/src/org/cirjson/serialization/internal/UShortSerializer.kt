package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object UShortSerializer : CircularKSerializer<UShort> {

    override val descriptor: CircularSerialDescriptor = InlinePrimitiveDescriptor("kotlin.UShort", Short.serializer())

    override fun serialize(encoder: CircularEncoder, value: UShort) {
        encoder.encodeInline(descriptor).encodeShort(value.toShort())
    }

    override fun deserialize(decoder: CircularDecoder): UShort {
        return decoder.decodeInline(descriptor).decodeShort().toUShort()
    }

}