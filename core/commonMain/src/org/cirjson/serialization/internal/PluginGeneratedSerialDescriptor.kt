package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.SerialKind
import org.cirjson.serialization.descriptors.StructureKind
import org.cirjson.serialization.encoding.CircularCompositeDecoder

/**
 * Implementation that plugin uses to implement descriptors for auto-generated serializers.
 */
@PublishedApi
@OptIn(ExperimentalCircularSerializationApi::class, InternalCircularSerializationApi::class)
internal open class PluginGeneratedSerialDescriptor(override val serialName: String,
        private val generatedSerializer: GeneratedCircularSerializer<*>? = null,
        final override val elementsCount: Int) : CircularSerialDescriptor, CircularCachedNames {

    override val kind: SerialKind get() = StructureKind.CLASS

    override val annotations: List<Annotation> get() = classAnnotations ?: emptyList()

    private var added = -1

    private val names = Array(elementsCount) { "[UNINITIALIZED]" }

    private val propertiesAnnotations = arrayOfNulls<MutableList<Annotation>?>(elementsCount)

    // Classes rarely have annotations, so we can save up a bit of allocations here
    private var classAnnotations: MutableList<Annotation>? = null

    private val elementsOptionality = BooleanArray(elementsCount)
    public override val serialNames: Set<String> get() = indices.keys

    private var indices: Map<String, Int> = emptyMap()

    // Cache child serializers, they are not cached by the implementation for nullable types
    private val childSerializers: Array<CircularKSerializer<*>> by lazy(
            LazyThreadSafetyMode.PUBLICATION) { generatedSerializer?.childSerializers() ?: EMPTY_SERIALIZER_ARRAY }

    // Lazy because of JS specific initialization order (#789)
    internal val typeParameterDescriptors: Array<CircularSerialDescriptor> by lazy(LazyThreadSafetyMode.PUBLICATION) {
        generatedSerializer?.typeParametersSerializers()?.map { it.descriptor }.compactArray()
    }

    // Can be without synchronization but Native will likely break due to freezing
    private val _hashCode: Int by lazy(LazyThreadSafetyMode.PUBLICATION) { hashCodeImpl(typeParameterDescriptors) }

    public fun addElement(name: String, isOptional: Boolean = false) {
        names[++added] = name
        elementsOptionality[added] = isOptional
        propertiesAnnotations[added] = null
        if (added == elementsCount - 1) {
            indices = buildIndices()
        }
    }

    public fun pushAnnotation(annotation: Annotation) {
        val list = propertiesAnnotations[added].let {
            if (it == null) {
                val result = ArrayList<Annotation>(1)
                propertiesAnnotations[added] = result
                result
            } else {
                it
            }
        }
        list.add(annotation)
    }

    public fun pushClassAnnotation(a: Annotation) {
        if (classAnnotations == null) {
            classAnnotations = ArrayList(1)
        }
        classAnnotations!!.add(a)
    }

    override fun getElementDescriptor(index: Int): CircularSerialDescriptor {
        return childSerializers.getChecked(index).descriptor
    }

    override fun isElementOptional(index: Int): Boolean = elementsOptionality.getChecked(index)

    override fun getElementAnnotations(index: Int): List<Annotation> =
            propertiesAnnotations.getChecked(index) ?: emptyList()

    override fun getElementName(index: Int): String = names.getChecked(index)

    override fun getElementIndex(name: String): Int = indices[name] ?: CircularCompositeDecoder.UNKNOWN_NAME

    private fun buildIndices(): Map<String, Int> {
        val indices = HashMap<String, Int>()
        for (i in names.indices) {
            indices[names[i]] = i
        }
        return indices
    }

    override fun equals(other: Any?): Boolean = equalsImpl(other) { otherDescriptor ->
        typeParameterDescriptors.contentEquals(otherDescriptor.typeParameterDescriptors)
    }

    override fun hashCode(): Int = _hashCode

    override fun toString(): String {
        return (0 until elementsCount).joinToString(", ", "$serialName(", ")") { i ->
            getElementName(i) + ": " + getElementDescriptor(i).serialName
        }
    }

}