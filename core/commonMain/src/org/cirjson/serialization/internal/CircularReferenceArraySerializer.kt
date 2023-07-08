package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import kotlin.reflect.KClass

// todo: can be more efficient when array size is know in advance, this one always uses temporary ArrayList as builder
@PublishedApi
@InternalCircularSerializationApi
internal class CircularReferenceArraySerializer<ElementKlass : Any, Element : ElementKlass?>(
        private val kClass: KClass<ElementKlass>, eSerializer: CircularKSerializer<Element>) :
    CircularCollectionLikeSerializer<Element, Array<Element>, ArrayList<Element>>(eSerializer) {

    override val descriptor: CircularSerialDescriptor = CircularArrayClassDesc(eSerializer.descriptor)

    override fun Array<Element>.collectionSize(): Int = size

    override fun Array<Element>.collectionIterator(): Iterator<Element> = iterator()

    override fun builder(): ArrayList<Element> = arrayListOf()

    override fun ArrayList<Element>.builderSize(): Int = size

    @Suppress("UNCHECKED_CAST")
    override fun ArrayList<Element>.toResult(): Array<Element> = toNativeArrayImpl<ElementKlass, Element>(kClass)

    override fun Array<Element>.toBuilder(): ArrayList<Element> = ArrayList(this.asList())

    override fun ArrayList<Element>.checkCapacity(size: Int): Unit = ensureCapacity(size)

    override fun ArrayList<Element>.insert(index: Int, element: Element) {
        add(index, element)
    }

}
