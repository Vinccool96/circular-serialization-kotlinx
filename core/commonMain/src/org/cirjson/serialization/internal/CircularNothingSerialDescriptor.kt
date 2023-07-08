package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.SerialKind
import org.cirjson.serialization.descriptors.StructureKind

@OptIn(ExperimentalCircularSerializationApi::class)
internal object CircularNothingSerialDescriptor : CircularSerialDescriptor {

    public override val kind: SerialKind = StructureKind.OBJECT

    public override val serialName: String = "kotlin.Nothing"

    override val elementsCount: Int get() = 0

    override fun getElementName(index: Int): String = error()

    override fun getElementIndex(name: String): Int = error()

    override fun isElementOptional(index: Int): Boolean = error()

    override fun getElementDescriptor(index: Int): CircularSerialDescriptor = error()

    override fun getElementAnnotations(index: Int): List<Annotation> = error()

    override fun toString(): String = "NothingSerialDescriptor"

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int = serialName.hashCode() + 31 * kind.hashCode()

    private fun error(): Nothing =
            throw IllegalStateException("Descriptor for type `kotlin.Nothing` does not have elements")

}