package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@PublishedApi
@InternalCircularSerializationApi
internal class CircularArrayListSerializer<E>(element: CircularKSerializer<E>) :
    CircularCollectionSerializer<E, List<E>, ArrayList<E>>(element) {

    override val descriptor: CircularSerialDescriptor = CircularArrayListClassDesc(element.descriptor)

    override fun builder(): ArrayList<E> = arrayListOf()

    override fun ArrayList<E>.builderSize(): Int = size

    override fun ArrayList<E>.toResult(): List<E> = this

    override fun List<E>.toBuilder(): ArrayList<E> = this as? ArrayList<E> ?: ArrayList(this)

    override fun ArrayList<E>.checkCapacity(size: Int): Unit = ensureCapacity(size)

    override fun ArrayList<E>.insert(index: Int, element: E) {
        add(index, element)
    }

}