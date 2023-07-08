package org.cirjson.serialization.encoding

import org.cirjson.serialization.descriptors.CircularSerialDescriptor

/**
 * Begins a structure, decodes it using the given [block], ends it and returns decoded element.
 */
public inline fun <T> CircularDecoder.decodeStructure(descriptor: CircularSerialDescriptor,
        crossinline block: CircularCompositeDecoder.() -> T): T {
    val composite = beginStructure(descriptor)
    val result = composite.block()
    composite.endStructure(descriptor)
    return result
}

/**
 * Begins a collection, encodes it using the given [block] and ends it.
 */
public inline fun CircularEncoder.encodeCollection(descriptor: CircularSerialDescriptor, collectionSize: Int,
        crossinline block: CircularCompositeEncoder.() -> Unit) {
    val composite = beginCollection(descriptor, collectionSize)
    composite.block()
    composite.endStructure(descriptor)
}

/**
 * Begins a collection, calls [block] with each item and ends the collections.
 */
public inline fun <E> CircularEncoder.encodeCollection(descriptor: CircularSerialDescriptor, collection: Collection<E>,
        crossinline block: CircularCompositeEncoder.(index: Int, E) -> Unit) {
    encodeCollection(descriptor, collection.size) {
        collection.forEachIndexed { index, e ->
            block(index, e)
        }
    }
}
