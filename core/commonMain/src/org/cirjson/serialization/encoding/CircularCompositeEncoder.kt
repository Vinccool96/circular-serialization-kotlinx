package org.cirjson.serialization.encoding

import org.cirjson.serialization.CircularSerializationStrategy
import org.cirjson.serialization.EncodeDefault
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.modules.CircularSerializersModule

/**
 * [CircularCompositeEncoder] is a part of encoding process that is bound to a particular structured part of
 * the serialized form, described by the serial descriptor passed to [CircularEncoder.beginStructure].
 *
 * All `encode*` methods have `index` and `serialDescriptor` parameters with a strict semantics and constraints:
 *   * `descriptor` is always the same as one used in [CircularEncoder.beginStructure]. While this parameter may seem redundant,
 *      it is required for efficient serialization process to avoid excessive field spilling.
 *      If you are writing your own format, you can safely ignore this parameter and use one used in `beginStructure`
 *      for simplicity.
 *   * `index` of the element being encoded. This element at this index in the descriptor should be associated with
 *      the one being written.
 *
 * The symmetric interface for the deserialization process is [CircularCompositeDecoder].
 *
 * ### Not stable for inheritance
 *
 * `CompositeEncoder` interface is not stable for inheritance in 3rd party libraries, as new methods
 * might be added to this interface or contracts of the existing methods can be changed.
 */
public interface CircularCompositeEncoder {

    /**
     * Context of the current serialization process, including contextual and polymorphic serialization and,
     * potentially, a format-specific configuration.
     */
    public val serializersModule: CircularSerializersModule

    /**
     * Denotes the end of the structure associated with current encoder.
     * For example, composite encoder of JSON format will write
     * a closing bracket in the underlying input and reduce the number of nesting for pretty printing.
     */
    public fun endStructure(descriptor: CircularSerialDescriptor)

    /**
     * Whether the format should encode values that are equal to the default values.
     * This method is used by plugin-generated serializers for properties with default values:
     * ```
     * @Serializable
     * class WithDefault(val int: Int = 42)
     * // serialize method
     * if (value.int != 42 || output.shouldEncodeElementDefault(serialDesc, 0)) {
     *    encoder.encodeIntElement(serialDesc, 0, value.int);
     * }
     * ```
     *
     * This method is never invoked for properties annotated with [EncodeDefault].
     */
    @ExperimentalCircularSerializationApi
    public fun shouldEncodeElementDefault(descriptor: CircularSerialDescriptor, index: Int): Boolean = true

    /**
     * Encodes a boolean [value] associated with an element at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.BOOLEAN] kind.
     */
    public fun encodeBooleanElement(descriptor: CircularSerialDescriptor, index: Int, value: Boolean)

    /**
     * Encodes a single byte [value] associated with an element at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.BYTE] kind.
     */
    public fun encodeByteElement(descriptor: CircularSerialDescriptor, index: Int, value: Byte)

    /**
     * Encodes a 16-bit short [value] associated with an element at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.SHORT] kind.
     */
    public fun encodeShortElement(descriptor: CircularSerialDescriptor, index: Int, value: Short)

    /**
     * Encodes a 16-bit unicode character [value] associated with an element at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.CHAR] kind.
     */
    public fun encodeCharElement(descriptor: CircularSerialDescriptor, index: Int, value: Char)

    /**
     * Encodes a 32-bit integer [value] associated with an element at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.INT] kind.
     */
    public fun encodeIntElement(descriptor: CircularSerialDescriptor, index: Int, value: Int)

    /**
     * Encodes a 64-bit integer [value] associated with an element at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.LONG] kind.
     */
    public fun encodeLongElement(descriptor: CircularSerialDescriptor, index: Int, value: Long)

    /**
     * Encodes a 32-bit IEEE 754 floating point [value] associated with an element
     * at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.FLOAT] kind.
     */
    public fun encodeFloatElement(descriptor: CircularSerialDescriptor, index: Int, value: Float)

    /**
     * Encodes a 64-bit IEEE 754 floating point [value] associated with an element
     * at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.DOUBLE] kind.
     */
    public fun encodeDoubleElement(descriptor: CircularSerialDescriptor, index: Int, value: Double)

    /**
     * Encodes a string [value] associated with an element at the given [index] in [serial descriptor][descriptor].
     * The element at the given [index] should have [PrimitiveKind.STRING] kind.
     */
    public fun encodeStringElement(descriptor: CircularSerialDescriptor, index: Int, value: String)

    /**
     * Returns [CircularEncoder] for decoding an underlying type of a value class in an inline manner.
     * Serializable value class is described by the [child descriptor][CircularSerialDescriptor.getElementDescriptor]
     * of given [descriptor] at [index].
     *
     * Namely, for the `@Serializable @JvmInline value class MyInt(val my: Int)`,
     * and `@Serializable class MyData(val myInt: MyInt)` the following sequence is used:
     * ```
     * thisEncoder.encodeInlineElement(MyData.serializer.descriptor, 0).encodeInt(my)
     * ```
     *
     * This method provides an opportunity for the optimization to avoid boxing of a carried value
     * and its invocation should be equivalent to the following:
     * ```
     * thisEncoder.encodeSerializableElement(MyData.serializer.descriptor, 0, MyInt.serializer(), myInt)
     * ```
     *
     * Current encoder may return any other instance of [CircularEncoder] class, depending on provided descriptor.
     * For example, when this function is called on Json encoder with descriptor that has
     * `UInt.serializer().descriptor` at the given [index], the returned encoder is able
     * to encode unsigned integers.
     *
     * Note that this function returns [CircularEncoder] instead of the [CircularCompositeEncoder]
     * because value classes always have the single property.
     * Calling [CircularEncoder.beginStructure] on returned instance leads to an unspecified behavior and, in general, is prohibited.
     *
     * @see CircularEncoder.encodeInline
     * @see CircularSerialDescriptor.getElementDescriptor
     */
    public fun encodeInlineElement(descriptor: CircularSerialDescriptor, index: Int): CircularEncoder

    /**
     * Delegates [value] encoding of the type [T] to the given [serializer].
     * [value] is associated with an element at the given [index] in [serial descriptor][descriptor].
     */
    public fun <T : Any?> encodeSerializableElement(descriptor: CircularSerialDescriptor, index: Int,
            serializer: CircularSerializationStrategy<T>, value: T)

    /**
     * Delegates nullable [value] encoding of the type [T] to the given [serializer].
     * [value] is associated with an element at the given [index] in [serial descriptor][descriptor].
     */
    @ExperimentalCircularSerializationApi
    public fun <T : Any> encodeNullableSerializableElement(descriptor: CircularSerialDescriptor, index: Int,
            serializer: CircularSerializationStrategy<T>, value: T?)

}