package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.*

/**
 * Base serializer for all serializers for primitive arrays.
 *
 * It exists only to avoid code duplication and should not be used or implemented directly.
 * Use concrete serializers ([CircularByteArraySerializer], etc) instead.
 */
@PublishedApi
@InternalCircularSerializationApi
internal abstract class CircularPrimitiveArraySerializer<Element, Array, Builder : CircularPrimitiveArrayBuilder<Array>> internal constructor(
        primitiveSerializer: CircularKSerializer<Element>) :
    CircularCollectionLikeSerializer<Element, Array, Builder>(primitiveSerializer) {

    final override val descriptor: CircularSerialDescriptor =
            CircularPrimitiveArrayDescriptor(primitiveSerializer.descriptor)

    final override fun Builder.builderSize(): Int = position

    final override fun Builder.toResult(): Array = build()

    final override fun Builder.checkCapacity(size: Int): Unit = ensureCapacity(size)

    final override fun Array.collectionIterator(): Iterator<Element> =
            error("This method lead to boxing and must not be used, use writeContents instead")

    final override fun Builder.insert(index: Int, element: Element): Unit =
            error("This method lead to boxing and must not be used, use Builder.append instead")

    final override fun builder(): Builder = empty().toBuilder()

    protected abstract fun empty(): Array

    abstract override fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: Builder,
            checkIndex: Boolean)

    protected abstract fun writeContent(encoder: CircularCompositeEncoder, content: Array, size: Int)

    final override fun serialize(encoder: CircularEncoder, value: Array) {
        val size = value.collectionSize()
        encoder.encodeCollection(descriptor, size) {
            writeContent(this, value, size)
        }
    }

    final override fun deserialize(decoder: CircularDecoder): Array = merge(decoder, null)

}
