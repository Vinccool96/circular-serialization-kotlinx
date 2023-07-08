package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import org.cirjson.serialization.encoding.encodeCollection

@PublishedApi
@InternalCircularSerializationApi
internal sealed class CircularCollectionLikeSerializer<Element, Collection, Builder>(
        private val elementSerializer: CircularKSerializer<Element>) :
    AbstractCircularCollectionSerializer<Element, Collection, Builder>() {

    protected abstract fun Builder.insert(index: Int, element: Element)

    abstract override val descriptor: CircularSerialDescriptor

    override fun serialize(encoder: CircularEncoder, value: Collection) {
        val size = value.collectionSize()
        encoder.encodeCollection(descriptor, size) {
            val iterator = value.collectionIterator()
            for (index in 0..<size) encodeSerializableElement(descriptor, index, elementSerializer, iterator.next())
        }
    }

    final override fun readAll(decoder: CircularCompositeDecoder, builder: Builder, startIndex: Int, size: Int) {
        require(size >= 0) { "Size must be known in advance when using READ_ALL" }
        for (index in 0..<size) readElement(decoder, startIndex + index, builder, checkIndex = false)
    }

    override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: Builder, checkIndex: Boolean) {
        builder.insert(index, decoder.decodeSerializableElement(descriptor, index, elementSerializer))
    }

}