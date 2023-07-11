package org.cirjson.serialization

import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularCompositeDecoder

/**
 * Deserialization strategy defines the serial form of a type [T], including its structural description,
 * declared by the [descriptor] and the actual deserialization process, defined by the implementation
 * of the [deserialize] method.
 *
 * [deserialize] method takes an instance of [CircularDecoder], and, knowing the serial form of the [T],
 * invokes primitive retrieval methods on the decoder and then transforms the received primitives
 * to an instance of [T].
 *
 * A serial form of the type is a transformation of the concrete instance into a sequence of primitive values
 * and vice versa. The serial form is not required to completely mimic the structure of the class, for example,
 * a specific implementation may represent multiple integer values as a single string, omit or add some
 * values that are present in the type, but not in the instance.
 *
 * For a more detailed explanation of the serialization process, please refer to [CircularKSerializer] documentation.
 */
public interface CircularDeserializationStrategy<out T> {

    /**
     * Describes the structure of the serializable representation of [T], that current
     * deserializer is able to deserialize.
     */
    public val descriptor: CircularSerialDescriptor

    /**
     * Deserializes the value of type [T] using the format that is represented by the given [decoder].
     * [deserialize] method is format-agnostic and operates with a high-level structured [CircularDecoder] API.
     * As long as most of the formats imply an arbitrary order of properties, deserializer should be able
     * to decode these properties in an arbitrary order and in a format-agnostic way.
     * For that purposes, [CircularCompositeDecoder.decodeElementIndex]-based loop is used: decoder firstly
     * signals property at which index it is ready to decode and then expects caller to decode
     * property with the given index.
     *
     * Throws [CircularSerializationException] if value cannot be deserialized.
     *
     * Example of deserialize method:
     * ```
     * class MyData(int: Int, stringList: List<String>, alwaysZero: Long)
     *
     * fun deserialize(decoder: Decoder): MyData = decoder.decodeStructure(descriptor) {
     *     // decodeStructure decodes beginning and end of the structure
     *     var int: Int? = null
     *     var list: List<String>? = null
     *     loop@ while (true) {
     *         when (val index = decodeElementIndex(descriptor)) {
     *             DECODE_DONE -> break@loop
     *             0 -> {
     *                 // Decode 'int' property as Int
     *                 int = decodeIntElement(descriptor, index = 0)
     *             }
     *             1 -> {
     *                 // Decode 'stringList' property as List<String>
     *                 list = decodeSerializableElement(descriptor, index = 1, serializer<List<String>>())
     *             }
     *             else -> throw SerializationException("Unexpected index $index")
     *         }
     *      }
     *     if (int == null || list == null) throwMissingFieldException()
     *     // Always use 0 as a value for alwaysZero property because we decided to do so.
     *     return MyData(int, list, alwaysZero = 0L)
     * }
     * ```
     *
     * @throws MissingFieldException if non-optional fields were not found during deserialization
     * @throws CircularSerializationException in case of any deserialization-specific error
     * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
     * @see CircularKSerializer for additional information about general contracts and exception specifics
     */
    public fun deserialize(decoder: CircularDecoder): T

}