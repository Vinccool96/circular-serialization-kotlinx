package org.cirjson.serialization

import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.SerialKind
import org.cirjson.serialization.descriptors.StructureKind
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import kotlin.test.fail

// Custom serializer
@Suppress("NAME_SHADOWING")
object CustomSerializer : CircularKSerializer<Custom> {

    override val descriptor = object : CircularSerialDescriptor {

        override val serialName = "org.cirjson.serialization.Custom"

        override val kind: SerialKind = StructureKind.CLASS

        override val elementsCount: Int get() = 2

        override fun getElementName(index: Int) = when (index) {
            0 -> "value1"
            1 -> "value2"
            else -> ""
        }

        override fun getElementIndex(name: String) = when (name) {
            "value1" -> 0
            "value2" -> 1
            else -> -1
        }

        override fun getElementAnnotations(index: Int): List<Annotation> = emptyList()

        override fun getElementDescriptor(index: Int): CircularSerialDescriptor = fail("Should not be called")

        override fun isElementOptional(index: Int): Boolean = false

    }

    override fun serialize(encoder: CircularEncoder, value: Custom) {
        val encoder = encoder.beginStructure(descriptor)
        encoder.encodeStringElement(descriptor, 0, value._value1)
        encoder.encodeIntElement(descriptor, 1, value._value2)
        encoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: CircularDecoder): Custom {
        val decoder = decoder.beginStructure(descriptor)
        if (decoder.decodeElementIndex(descriptor) != 0) throw java.lang.IllegalStateException()
        val value1 = decoder.decodeStringElement(descriptor, 0)
        if (decoder.decodeElementIndex(descriptor) != 1) throw java.lang.IllegalStateException()
        val value2 = decoder.decodeIntElement(descriptor, 1)
        if (decoder.decodeElementIndex(
                        descriptor) != CircularCompositeDecoder.DECODE_DONE) throw java.lang.IllegalStateException()
        decoder.endStructure(descriptor)
        return Custom(value1, value2)
    }

}