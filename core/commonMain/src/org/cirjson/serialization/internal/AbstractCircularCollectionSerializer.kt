package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder

@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
public sealed class AbstractCircularCollectionSerializer<Element, Collection, Builder> :
    CircularKSerializer<Collection> {

    protected abstract fun Collection.collectionSize(): Int

    protected abstract fun Collection.collectionIterator(): Iterator<Element>

    protected abstract fun builder(): Builder

    protected abstract fun Builder.builderSize(): Int

    protected abstract fun Builder.toResult(): Collection

    protected abstract fun Collection.toBuilder(): Builder

    protected abstract fun Builder.checkCapacity(size: Int)

    abstract override fun serialize(encoder: CircularEncoder, value: Collection)

    @InternalCircularSerializationApi
    public fun merge(decoder: CircularDecoder, previous: Collection?): Collection {
        val builder = previous?.toBuilder() ?: builder()
        val startIndex = builder.builderSize()
        val compositeDecoder = decoder.beginStructure(descriptor)
        if (compositeDecoder.decodeSequentially()) {
            readAll(compositeDecoder, builder, startIndex, readSize(compositeDecoder, builder))
        } else {
            while (true) {
                val index = compositeDecoder.decodeElementIndex(descriptor)
                if (index == CircularCompositeDecoder.DECODE_DONE) break
                readElement(compositeDecoder, startIndex + index, builder)
            }
        }
        compositeDecoder.endStructure(descriptor)
        return builder.toResult()
    }

    override fun deserialize(decoder: CircularDecoder): Collection = merge(decoder, null)

    private fun readSize(decoder: CircularCompositeDecoder, builder: Builder): Int {
        val size = decoder.decodeCollectionSize(descriptor)
        builder.checkCapacity(size)
        return size
    }

    protected abstract fun readElement(decoder: CircularCompositeDecoder, index: Int, builder: Builder,
            checkIndex: Boolean = true)

    protected abstract fun readAll(decoder: CircularCompositeDecoder, builder: Builder, startIndex: Int, size: Int)

}