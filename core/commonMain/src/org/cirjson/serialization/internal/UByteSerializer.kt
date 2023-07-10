package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object UByteSerializer : CircularKSerializer<UByte> {

    override val descriptor: CircularSerialDescriptor = InlinePrimitiveDescriptor("kotlin.UByte", Byte.serializer())

    override fun serialize(encoder: CircularEncoder, value: UByte) {
        encoder.encodeInline(descriptor).encodeByte(value.toByte())
    }

    override fun deserialize(decoder: CircularDecoder): UByte {
        return decoder.decodeInline(descriptor).decodeByte().toUByte()
    }

}