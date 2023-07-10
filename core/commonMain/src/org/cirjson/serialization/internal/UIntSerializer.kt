package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object UIntSerializer : CircularKSerializer<UInt> {

    override val descriptor: CircularSerialDescriptor = InlinePrimitiveDescriptor("kotlin.UInt", Int.serializer())

    override fun serialize(encoder: CircularEncoder, value: UInt) {
        encoder.encodeInline(descriptor).encodeInt(value.toInt())
    }

    override fun deserialize(decoder: CircularDecoder): UInt {
        return decoder.decodeInline(descriptor).decodeInt().toUInt()
    }

}