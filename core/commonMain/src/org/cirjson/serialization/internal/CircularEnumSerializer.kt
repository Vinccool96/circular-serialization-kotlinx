package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularEnumSerializer<T : Enum<T>>(serialName: String, private val values: Array<T>) :
    CircularKSerializer<T> {

    private var overriddenDescriptor: CircularSerialDescriptor? = null

    internal constructor(serialName: String, values: Array<T>, descriptor: CircularSerialDescriptor) : this(serialName,
            values) {
        overriddenDescriptor = descriptor
    }

    override val descriptor: CircularSerialDescriptor by lazy {
        overriddenDescriptor ?: createUnmarkedDescriptor(serialName)
    }

    private fun createUnmarkedDescriptor(serialName: String): CircularSerialDescriptor {
        val d = CircularEnumDescriptor(serialName, values.size)
        values.forEach { d.addElement(it.name) }
        return d
    }

    override fun serialize(encoder: CircularEncoder, value: T) {
        val index = values.indexOf(value)
        if (index == -1) {
            throw CircularSerializationException(
                    "$value is not a valid enum ${descriptor.serialName}, " + "must be one of ${values.contentToString()}")
        }
        encoder.encodeEnum(descriptor, index)
    }

    override fun deserialize(decoder: CircularDecoder): T {
        val index = decoder.decodeEnum(descriptor)
        if (index !in values.indices) {
            throw CircularSerializationException(
                    "$index is not among valid ${descriptor.serialName} enum values, " + "values size is ${values.size}")
        }
        return values[index]
    }

    override fun toString(): String = "kotlinx.serialization.internal.EnumSerializer<${descriptor.serialName}>"

}