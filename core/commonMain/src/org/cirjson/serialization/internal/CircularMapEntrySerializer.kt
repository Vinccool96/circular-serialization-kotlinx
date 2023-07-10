package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.StructureKind
import org.cirjson.serialization.descriptors.buildSerialDescriptor

@PublishedApi
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@OptIn(ExperimentalCircularSerializationApi::class, InternalCircularSerializationApi::class)
internal class CircularMapEntrySerializer<K, V>(keySerializer: CircularKSerializer<K>,
        valueSerializer: CircularKSerializer<V>) :
    CircularKeyValueSerializer<K, V, Map.Entry<K, V>>(keySerializer, valueSerializer) {

    private data class MapEntry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>

    /*
     * Kind 'MAP' because it is represented in a map-like manner with "key: value" serialized directly
     */
    override val descriptor: CircularSerialDescriptor =
            buildSerialDescriptor("kotlin.collections.Map.Entry", StructureKind.MAP) {
                element("key", keySerializer.descriptor)
                element("value", valueSerializer.descriptor)
            }

    override val Map.Entry<K, V>.key: K get() = this.key

    override val Map.Entry<K, V>.value: V get() = this.value

    override fun toResult(key: K, value: V): Map.Entry<K, V> = MapEntry(key, value)

}
