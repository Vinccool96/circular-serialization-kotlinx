package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@PublishedApi
@InternalCircularSerializationApi
internal class CircularLinkedHashSetSerializer<E>(eSerializer: CircularKSerializer<E>) :
    CircularCollectionSerializer<E, Set<E>, LinkedHashSet<E>>(eSerializer) {

    override val descriptor: CircularSerialDescriptor = CircularLinkedHashSetClassDesc(eSerializer.descriptor)

    override fun builder(): LinkedHashSet<E> = linkedSetOf()

    override fun LinkedHashSet<E>.builderSize(): Int = size

    override fun LinkedHashSet<E>.toResult(): Set<E> = this

    override fun Set<E>.toBuilder(): LinkedHashSet<E> = this as? LinkedHashSet<E> ?: LinkedHashSet(this)

    override fun LinkedHashSet<E>.checkCapacity(size: Int) {}

    override fun LinkedHashSet<E>.insert(index: Int, element: E) {
        add(element)
    }

}