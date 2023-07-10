package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularSerialDescriptorForNullable(internal val original: CircularSerialDescriptor) :
    CircularSerialDescriptor by original, CircularCachedNames {

    override val serialName: String = original.serialName + "?"

    override val serialNames: Set<String> = original.cachedSerialNames()

    override val isNullable: Boolean
        get() = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CircularSerialDescriptorForNullable) return false
        return original == other.original
    }

    override fun toString(): String {
        return "$original?"
    }

    override fun hashCode(): Int {
        return original.hashCode() * 31
    }

}