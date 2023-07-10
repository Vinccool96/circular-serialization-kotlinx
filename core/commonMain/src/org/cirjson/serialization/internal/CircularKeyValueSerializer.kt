package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.StructureKind
import org.cirjson.serialization.descriptors.buildCircularClassSerialDescriptor
import org.cirjson.serialization.descriptors.buildSerialDescriptor
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import org.cirjson.serialization.encoding.decodeStructure

@PublishedApi
@OptIn(ExperimentalCircularSerializationApi::class)
internal sealed class CircularKeyValueSerializer<K, V, R>(protected val keySerializer: CircularKSerializer<K>,
        protected val valueSerializer: CircularKSerializer<V>) : CircularKSerializer<R> {

    protected abstract val R.key: K
    protected abstract val R.value: V
    protected abstract fun toResult(key: K, value: V): R

    override fun serialize(encoder: CircularEncoder, value: R) {
        val structuredEncoder = encoder.beginStructure(descriptor)
        structuredEncoder.encodeSerializableElement(descriptor, 0, keySerializer, value.key)
        structuredEncoder.encodeSerializableElement(descriptor, 1, valueSerializer, value.value)
        structuredEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: CircularDecoder): R = decoder.decodeStructure(descriptor) {
        if (decodeSequentially()) {
            val key = decodeSerializableElement(descriptor, 0, keySerializer)
            val value = decodeSerializableElement(descriptor, 1, valueSerializer)
            return@decodeStructure toResult(key, value)
        }

        var key: Any? = NULL
        var value: Any? = NULL
        mainLoop@ while (true) {
            when (val idx = decodeElementIndex(descriptor)) {
                CircularCompositeDecoder.DECODE_DONE -> {
                    break@mainLoop
                }
                0 -> {
                    key = decodeSerializableElement(descriptor, 0, keySerializer)
                }
                1 -> {
                    value = decodeSerializableElement(descriptor, 1, valueSerializer)
                }
                else -> throw CircularSerializationException("Invalid index: $idx")
            }
        }
        if (key === NULL) throw CircularSerializationException("Element 'key' is missing")
        if (value === NULL) throw CircularSerializationException("Element 'value' is missing")
        @Suppress("UNCHECKED_CAST") return@decodeStructure toResult(key as K, value as V)
    }
}
