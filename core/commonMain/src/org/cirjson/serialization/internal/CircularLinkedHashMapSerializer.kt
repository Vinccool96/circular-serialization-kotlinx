package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@PublishedApi
@InternalCircularSerializationApi
internal class CircularLinkedHashMapSerializer<K, V>(kSerializer: CircularKSerializer<K>,
        vSerializer: CircularKSerializer<V>) :
    CircularMapLikeSerializer<K, V, Map<K, V>, LinkedHashMap<K, V>>(kSerializer, vSerializer) {

    override val descriptor: CircularSerialDescriptor =
            CircularLinkedHashMapClassDesc(kSerializer.descriptor, vSerializer.descriptor)

    override fun Map<K, V>.collectionSize(): Int = size

    override fun Map<K, V>.collectionIterator(): Iterator<Map.Entry<K, V>> = iterator()

    override fun builder(): LinkedHashMap<K, V> = LinkedHashMap()

    override fun LinkedHashMap<K, V>.builderSize(): Int = size * 2

    override fun LinkedHashMap<K, V>.toResult(): Map<K, V> = this

    override fun Map<K, V>.toBuilder(): LinkedHashMap<K, V> = this as? LinkedHashMap<K, V> ?: LinkedHashMap(this)

    override fun LinkedHashMap<K, V>.checkCapacity(size: Int) {}

    override fun LinkedHashMap<K, V>.insertKeyValuePair(index: Int, key: K, value: V): Unit = set(key, value)

}
