package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.SerialInfo

/**
 * Builder for [SerialDescriptor] for user-defined serializers.
 *
 * Both explicit builder functions and implicit (using reified type-parameters) are present and are equivalent.
 * For example, `element<Int?>("nullableIntField")` is indistinguishable from
 * `element("nullableIntField", IntSerializer.descriptor.nullable)` and
 * from `element("nullableIntField", descriptor<Int?>)`.
 *
 * Please refer to [SerialDescriptor] builder function for a complete example.
 */
public class CircularClassSerialDescriptorBuilder internal constructor(public val serialName: String) {

    /**
     * Indicates that serializer associated with the current serial descriptor
     * support nullable types, meaning that it should declare nullable type
     * in its [CircularKSerializer] type parameter and handle nulls during encoding and decoding.
     */
    @ExperimentalCircularSerializationApi
    @Deprecated(
            "isNullable inside buildSerialDescriptor is deprecated. Please use SerialDescriptor.nullable extension on a builder result.",
            level = DeprecationLevel.ERROR)
    public var isNullable: Boolean = false

    /**
     * [Serial][SerialInfo] annotations on a target type.
     */
    @ExperimentalCircularSerializationApi
    public var annotations: List<Annotation> = emptyList()

    internal val elementNames: MutableList<String> = ArrayList()

    private val uniqueNames: MutableSet<String> = HashSet()

    internal val elementDescriptors: MutableList<CircularSerialDescriptor> = ArrayList()

    internal val elementAnnotations: MutableList<List<Annotation>> = ArrayList()

    internal val elementOptionality: MutableList<Boolean> = ArrayList()

    /**
     * Add an element with a given [name][elementName], [descriptor],
     * type annotations and optionality the resulting descriptor.
     *
     * Example of usage:
     * ```
     * class Data(
     *     val intField: Int? = null, // Optional, has default value
     *     @ProtoNumber(1) val longField: Long
     * )
     *
     * // Corresponding descriptor
     * SerialDescriptor("package.Data") {
     *     element<Int?>("intField", isOptional = true)
     *     element<Long>("longField", annotations = listOf(protoIdAnnotationInstance))
     * }
     * ```
     */
    public fun element(elementName: String, descriptor: CircularSerialDescriptor,
            annotations: List<Annotation> = emptyList(), isOptional: Boolean = false) {
        require(uniqueNames.add(
                elementName)) { "Element with name '$elementName' is already registered in $serialName" }
        elementNames += elementName
        elementDescriptors += descriptor
        elementAnnotations += annotations
        elementOptionality += isOptional
    }

}