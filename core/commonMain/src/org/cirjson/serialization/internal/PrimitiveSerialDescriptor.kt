package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalCircularSerializationApi::class)
internal class PrimitiveSerialDescriptor(override val serialName: String, override val kind: PrimitiveKind) :
    CircularSerialDescriptor {

    override val elementsCount: Int get() = 0

    override fun getElementName(index: Int): String = error()

    override fun getElementIndex(name: String): Int = error()

    override fun isElementOptional(index: Int): Boolean = error()

    override fun getElementDescriptor(index: Int): CircularSerialDescriptor = error()

    override fun getElementAnnotations(index: Int): List<Annotation> = error()

    override fun toString(): String = "PrimitiveDescriptor($serialName)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrimitiveSerialDescriptor) return false
        return serialName == other.serialName && kind == other.kind
    }

    override fun hashCode() = serialName.hashCode() + 31 * kind.hashCode()

    private fun error(): Nothing = throw IllegalStateException("Primitive descriptor does not have elements")

}