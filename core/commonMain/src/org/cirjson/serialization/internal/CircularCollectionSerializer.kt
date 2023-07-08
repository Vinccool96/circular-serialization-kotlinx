package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi

@PublishedApi
@InternalCircularSerializationApi
internal abstract class CircularCollectionSerializer<E, C : Collection<E>, B>(element: CircularKSerializer<E>) :
    CircularCollectionLikeSerializer<E, C, B>(element) {

    override fun C.collectionSize(): Int = size

    override fun C.collectionIterator(): Iterator<E> = iterator()

}
