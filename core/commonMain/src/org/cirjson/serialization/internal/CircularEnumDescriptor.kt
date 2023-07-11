package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.*

/*
 * Descriptor used for explicitly serializable enums by the plugin.
 * Designed to be consistent with `EnumSerializer.descriptor` and weird plugin usage.
 */
@Suppress("unused") // Used by the plugin
@PublishedApi
@OptIn(ExperimentalCircularSerializationApi::class, InternalCircularSerializationApi::class)
internal class CircularEnumDescriptor(name: String, elementsCount: Int) :
    PluginGeneratedSerialDescriptor(name, elementsCount = elementsCount) {

    override val kind: SerialKind = SerialKind.ENUM

    @OptIn(InternalCircularSerializationApi::class)
    private val elementDescriptors by lazy {
        Array(elementsCount) { buildSerialDescriptor(name + "." + getElementName(it), StructureKind.OBJECT) }
    }

    override fun getElementDescriptor(index: Int): CircularSerialDescriptor = elementDescriptors.getChecked(index)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (other !is CircularSerialDescriptor) return false
        if (other.kind !== SerialKind.ENUM) return false
        if (serialName != other.serialName) return false
        return cachedSerialNames() == other.cachedSerialNames()
    }

    override fun toString(): String {
        return elementNames.joinToString(", ", "$serialName(", ")")
    }

    override fun hashCode(): Int {
        var result = serialName.hashCode()
        val elementsHashCode = elementNames.elementsHashCodeBy { it }
        result = 31 * result + elementsHashCode
        return result
    }

}