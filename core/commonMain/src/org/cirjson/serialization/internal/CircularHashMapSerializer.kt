package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@PublishedApi
@InternalCircularSerializationApi
internal class CircularHashMapSerializer<K, V>(kSerializer: CircularKSerializer<K>,
        vSerializer: CircularKSerializer<V>) :
    CircularMapLikeSerializer<K, V, Map<K, V>, HashMap<K, V>>(kSerializer, vSerializer) {

    override val descriptor: CircularSerialDescriptor =
            CircularHashMapClassDesc(kSerializer.descriptor, vSerializer.descriptor)

    override fun Map<K, V>.collectionSize(): Int = size

    override fun Map<K, V>.collectionIterator(): Iterator<Map.Entry<K, V>> = iterator()

    override fun builder(): HashMap<K, V> = HashMap()

    override fun HashMap<K, V>.builderSize(): Int = size * 2

    override fun HashMap<K, V>.toResult(): Map<K, V> = this

    override fun Map<K, V>.toBuilder(): HashMap<K, V> = this as? HashMap<K, V> ?: HashMap(this)

    override fun HashMap<K, V>.checkCapacity(size: Int) {}

    override fun HashMap<K, V>.insertKeyValuePair(index: Int, key: K, value: V): Unit = set(key, value)

}
