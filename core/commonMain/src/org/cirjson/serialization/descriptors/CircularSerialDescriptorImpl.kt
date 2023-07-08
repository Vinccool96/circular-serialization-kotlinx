package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.internal.CircularCachedNames

@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularSerialDescriptorImpl(override val serialName: String, override val kind: SerialKind,
        override val elementsCount: Int, typeParameters: List<CircularSerialDescriptor>,
        builder: CircularClassSerialDescriptorBuilder) : CircularSerialDescriptor, CircularCachedNames {

    override val annotations: List<Annotation> = builder.annotations

    override val serialNames: Set<String> = builder.elementNames.toHashSet()

    private val elementNames: Array<String> = builder.elementNames.toTypedArray()

    private val elementDescriptors: Array<CircularSerialDescriptor> = builder.elementDescriptors.compactArray()

    private val elementAnnotations: Array<List<Annotation>> = builder.elementAnnotations.toTypedArray()

    private val elementOptionality: BooleanArray = builder.elementOptionality.toBooleanArray()

    private val name2Index: Map<String, Int> = elementNames.withIndex().map { it.value to it.index }.toMap()

    private val typeParametersDescriptors: Array<CircularSerialDescriptor> = typeParameters.compactArray()

    private val _hashCode: Int by lazy { hashCodeImpl(typeParametersDescriptors) }

    override fun getElementName(index: Int): String = elementNames.getChecked(index)

    override fun getElementIndex(name: String): Int = name2Index[name] ?: CircularCompositeDecoder.UNKNOWN_NAME

    override fun getElementAnnotations(index: Int): List<Annotation> = elementAnnotations.getChecked(index)

    override fun getElementDescriptor(index: Int): CircularSerialDescriptor = elementDescriptors.getChecked(index)

    override fun isElementOptional(index: Int): Boolean = elementOptionality.getChecked(index)

    override fun equals(other: Any?): Boolean = equalsImpl(other) { otherDescriptor: CircularSerialDescriptorImpl ->
        typeParametersDescriptors.contentEquals(otherDescriptor.typeParametersDescriptors)
    }

    override fun hashCode(): Int = _hashCode

    override fun toString(): String {
        return (0..<elementsCount).joinToString(", ", prefix = "$serialName(", postfix = ")") {
            getElementName(it) + ": " + getElementDescriptor(it).serialName
        }
    }
}