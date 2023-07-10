package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal object ByteSerializer : CircularKSerializer<Byte> {

    override val descriptor: CircularSerialDescriptor =
            PrimitiveSerialDescriptor("kotlin.Byte", PrimitiveKind.BYTE)

    override fun serialize(encoder: CircularEncoder, value: Byte): Unit = encoder.encodeByte(value)

    override fun deserialize(decoder: CircularDecoder): Byte = decoder.decodeByte()

}