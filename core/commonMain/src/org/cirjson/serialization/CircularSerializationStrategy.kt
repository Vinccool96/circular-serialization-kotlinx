package org.cirjson.serialization

/**
 * Serialization strategy defines the serial form of a type [T], including its structural description,
 * declared by the [descriptor] and the actual serialization process, defined by the implementation
 * of the [serialize] method.
 *
 * [serialize] method takes an instance of [T] and transforms it into its serial form (a sequence of primitives),
 * calling the corresponding [Encoder] methods.
 *
 * A serial form of the type is a transformation of the concrete instance into a sequence of primitive values
 * and vice versa. The serial form is not required to completely mimic the structure of the class, for example,
 * a specific implementation may represent multiple integer values as a single string, omit or add some
 * values that are present in the type, but not in the instance.
 *
 * For a more detailed explanation of the serialization process, please refer to [CircularKSerializer] documentation.
 */
public interface CircularSerializationStrategy<in T> {

    /**
     * Describes the structure of the serializable representation of [T], produced
     * by this serializer.
     */
    public val descriptor: SerialDescriptor

    /**
     * Serializes the [value] of type [T] using the format that is represented by the given [encoder].
     * [serialize] method is format-agnostic and operates with a high-level structured [Encoder] API.
     * Throws [SerializationException] if value cannot be serialized.
     *
     * Example of serialize method:
     * ```
     * class MyData(int: Int, stringList: List<String>, alwaysZero: Long)
     *
     * fun serialize(encoder: Encoder, value: MyData): Unit = encoder.encodeStructure(descriptor) {
     *     // encodeStructure encodes beginning and end of the structure
     *     // encode 'int' property as Int
     *     encodeIntElement(descriptor, index = 0, value.int)
     *     // encode 'stringList' property as List<String>
     *     encodeSerializableElement(descriptor, index = 1, serializer<List<String>>, value.stringList)
     *     // don't encode 'alwaysZero' property because we decided to do so
     * } // end of the structure
     * ```
     *
     * @throws SerializationException in case of any serialization-specific error
     * @throws IllegalArgumentException if the supplied input does not comply encoder's specification
     * @see CircularKSerializer for additional information about general contracts and exception specifics
     */
    public fun serialize(encoder: Encoder, value: T)

}