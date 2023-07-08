package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@PublishedApi
@InternalCircularSerializationApi
internal class CircularHashSetSerializer<E>(eSerializer: CircularKSerializer<E>) :
    CircularCollectionSerializer<E, Set<E>, HashSet<E>>(eSerializer) {

    override val descriptor: CircularSerialDescriptor = CircularHashSetClassDesc(eSerializer.descriptor)

    override fun builder(): HashSet<E> = HashSet()

    override fun HashSet<E>.builderSize(): Int = size

    override fun HashSet<E>.toResult(): Set<E> = this

    override fun Set<E>.toBuilder(): HashSet<E> = this as? HashSet<E> ?: HashSet(this)

    override fun HashSet<E>.checkCapacity(size: Int) {}

    override fun HashSet<E>.insert(index: Int, element: E) {
        add(element)
    }

}