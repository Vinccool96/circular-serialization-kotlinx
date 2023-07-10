package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.buildCircularClassSerialDescriptor
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@PublishedApi
internal class CircularTripleSerializer<A, B, C>(private val aSerializer: CircularKSerializer<A>,
        private val bSerializer: CircularKSerializer<B>, private val cSerializer: CircularKSerializer<C>) :
    CircularKSerializer<Triple<A, B, C>> {

    override val descriptor: CircularSerialDescriptor = buildCircularClassSerialDescriptor("kotlin.Triple") {
        element("first", aSerializer.descriptor)
        element("second", bSerializer.descriptor)
        element("third", cSerializer.descriptor)
    }

    override fun serialize(encoder: CircularEncoder, value: Triple<A, B, C>) {
        val structuredEncoder = encoder.beginStructure(descriptor)
        structuredEncoder.encodeSerializableElement(descriptor, 0, aSerializer, value.first)
        structuredEncoder.encodeSerializableElement(descriptor, 1, bSerializer, value.second)
        structuredEncoder.encodeSerializableElement(descriptor, 2, cSerializer, value.third)
        structuredEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: CircularDecoder): Triple<A, B, C> {
        val composite = decoder.beginStructure(descriptor)
        if (composite.decodeSequentially()) {
            return decodeSequentially(composite)
        }
        return decodeStructure(composite)
    }

    private fun decodeSequentially(composite: CircularCompositeDecoder): Triple<A, B, C> {
        val a = composite.decodeSerializableElement(descriptor, 0, aSerializer)
        val b = composite.decodeSerializableElement(descriptor, 1, bSerializer)
        val c = composite.decodeSerializableElement(descriptor, 2, cSerializer)
        composite.endStructure(descriptor)
        return Triple(a, b, c)
    }

    private fun decodeStructure(composite: CircularCompositeDecoder): Triple<A, B, C> {
        var a: Any? = NULL
        var b: Any? = NULL
        var c: Any? = NULL
        mainLoop@ while (true) {
            when (val index = composite.decodeElementIndex(descriptor)) {
                CircularCompositeDecoder.DECODE_DONE -> {
                    break@mainLoop
                }
                0 -> {
                    a = composite.decodeSerializableElement(descriptor, 0, aSerializer)
                }
                1 -> {
                    b = composite.decodeSerializableElement(descriptor, 1, bSerializer)
                }
                2 -> {
                    c = composite.decodeSerializableElement(descriptor, 2, cSerializer)
                }
                else -> throw CircularSerializationException("Unexpected index $index")
            }
        }
        composite.endStructure(descriptor)
        if (a === NULL) throw CircularSerializationException("Element 'first' is missing")
        if (b === NULL) throw CircularSerializationException("Element 'second' is missing")
        if (c === NULL) throw CircularSerializationException("Element 'third' is missing")
        @Suppress("UNCHECKED_CAST") return Triple(a as A, b as B, c as C)
    }

}