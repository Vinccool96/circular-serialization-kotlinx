package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.buildCircularClassSerialDescriptor

@PublishedApi
internal class CircularPairSerializer<K, V>(keySerializer: CircularKSerializer<K>, valueSerializer: CircularKSerializer<V>) :
    CircularKeyValueSerializer<K, V, Pair<K, V>>(keySerializer, valueSerializer) {

    override val descriptor: CircularSerialDescriptor = buildCircularClassSerialDescriptor("kotlin.Pair") {
        element("first", keySerializer.descriptor)
        element("second", valueSerializer.descriptor)
    }

    override val Pair<K, V>.key: K get() = this.first

    override val Pair<K, V>.value: V get() = this.second

    override fun toResult(key: K, value: V): Pair<K, V> = key to value

}
