package org.cirjson.serialization.internal

import org.cirjson.serialization.*
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

/**
 * Use [CircularKSerializer.nullable][nullable] instead.
 * @suppress internal API
 */
@PublishedApi
@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularNullableSerializer<T : Any>(private val serializer: CircularKSerializer<T>) :
    CircularKSerializer<T?> {

    override val descriptor: CircularSerialDescriptor = CircularSerialDescriptorForNullable(serializer.descriptor)

    override fun serialize(encoder: CircularEncoder, value: T?) {
        if (value != null) {
            encoder.encodeNotNullMark()
            encoder.encodeSerializableValue(serializer, value)
        } else {
            encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: CircularDecoder): T? {
        return if (decoder.decodeNotNullMark()) decoder.decodeSerializableValue(serializer) else decoder.decodeNull()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as CircularNullableSerializer<*>
        return serializer == other.serializer
    }

    override fun hashCode(): Int {
        return serializer.hashCode()
    }
}