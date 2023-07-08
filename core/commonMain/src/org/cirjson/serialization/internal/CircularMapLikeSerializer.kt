package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import org.cirjson.serialization.encoding.encodeCollection

@InternalCircularSerializationApi // TODO tech debt: it's used in ProtoBuf
public sealed class CircularMapLikeSerializer<Key, Value, Collection, Builder : MutableMap<Key, Value>>(
        public val keySerializer: CircularKSerializer<Key>, public val valueSerializer: CircularKSerializer<Value>) :
    AbstractCircularCollectionSerializer<Map.Entry<Key, Value>, Collection, Builder>() {

    protected abstract fun Builder.insertKeyValuePair(index: Int, key: Key, value: Value)
    abstract override val descriptor: CircularSerialDescriptor

    protected final override fun readAll(decoder: CircularCompositeDecoder, builder: Builder, startIndex: Int,
            size: Int) {
        require(size >= 0) { "Size must be known in advance when using READ_ALL" }
        for (index in 0 until size * 2 step 2) readElement(decoder, startIndex + index, builder, checkIndex = false)
    }

    final override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: Builder,
            checkIndex: Boolean) {
        val key: Key = decoder.decodeSerializableElement(descriptor, index, keySerializer)
        val vIndex = if (checkIndex) {
            decoder.decodeElementIndex(descriptor).also {
                require(it == index + 1) { "Value must follow key in a map, index for key: $index, returned index for value: $it" }
            }
        } else {
            index + 1
        }
        val value: Value = if (builder.containsKey(key) && valueSerializer.descriptor.kind !is PrimitiveKind) {
            decoder.decodeSerializableElement(descriptor, vIndex, valueSerializer, builder.getValue(key))
        } else {
            decoder.decodeSerializableElement(descriptor, vIndex, valueSerializer)
        }
        builder[key] = value
    }

    override fun serialize(encoder: CircularEncoder, value: Collection) {
        val size = value.collectionSize()
        encoder.encodeCollection(descriptor, size) {
            val iterator = value.collectionIterator()
            var index = 0
            iterator.forEach { (k, v) ->
                encodeSerializableElement(descriptor, index++, keySerializer, k)
                encodeSerializableElement(descriptor, index++, valueSerializer, v)
            }
        }
    }

}