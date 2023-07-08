package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.SerialKind
import org.cirjson.serialization.descriptors.StructureKind

@ExperimentalCircularSerializationApi
internal sealed class CircularListLikeDescriptor(val elementDescriptor: CircularSerialDescriptor) : CircularSerialDescriptor {

    override val kind: SerialKind get() = StructureKind.LIST

    override val elementsCount: Int = 1

    override fun getElementName(index: Int): String = index.toString()

    override fun getElementIndex(name: String): Int =
            name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid list index")

    override fun isElementOptional(index: Int): Boolean {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return false
    }

    override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return emptyList()
    }

    override fun getElementDescriptor(index: Int): CircularSerialDescriptor {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return elementDescriptor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CircularListLikeDescriptor) return false
        return elementDescriptor == other.elementDescriptor && serialName == other.serialName
    }

    override fun hashCode(): Int {
        return elementDescriptor.hashCode() * 31 + serialName.hashCode()
    }

    override fun toString(): String = "$serialName($elementDescriptor)"

}