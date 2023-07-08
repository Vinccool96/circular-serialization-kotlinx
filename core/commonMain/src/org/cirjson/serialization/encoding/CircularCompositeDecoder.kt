package org.cirjson.serialization.encoding

import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.modules.CircularSerializersModule

/**
 * [CircularCompositeDecoder] is a part of decoding process that is bound to a particular structured part of
 * the serialized form, described by the serial descriptor passed to [CircularDecoder.beginStructure].
 *
 * Typically, for unordered data, [CircularCompositeDecoder] is used by a serializer withing a [decodeElementIndex]-based
 * loop that decodes all the required data one-by-one in any order and then terminates by calling [endStructure].
 * Please refer to [decodeElementIndex] for example of such loop.
 *
 * All `decode*` methods have `index` and `serialDescriptor` parameters with a strict semantics and constraints:
 *   * `descriptor` argument is always the same as one used in [CircularDecoder.beginStructure].
 *   * `index` of the element being decoded. For [sequential][decodeSequentially] decoding, it is always a monotonic
 *      sequence from `0` to `descriptor.elementsCount` and for indexing-loop it is always an index that [decodeElementIndex]
 *      has returned from the last call.
 *
 * The symmetric interface for the serialization process is [CircularCompositeEncoder].
 *
 * ### Not stable for inheritance
 *
 * `CompositeDecoder` interface is not stable for inheritance in 3rd party libraries, as new methods
 * might be added to this interface or contracts of the existing methods can be changed.
 */
public interface CircularCompositeDecoder {

    /**
     * Results of [decodeElementIndex] used for decoding control flow.
     */
    public companion object {
        /**
         * Value returned by [decodeElementIndex] when the underlying input has no more data in the current structure.
         * When this value is returned, no methods of the decoder should be called but [endStructure].
         */
        public const val DECODE_DONE: Int = -1

        /**
         * Value returned by [decodeElementIndex] when the format encountered an unknown element
         * (expected neither by the structure of serial descriptor, nor by the format itself).
         */
        public const val UNKNOWN_NAME: Int = -3
    }

    /**
     * Context of the current decoding process, including contextual and polymorphic serialization and,
     * potentially, a format-specific configuration.
     */
    public val serializersModule: CircularSerializersModule

    /**
     * Denotes the end of the structure associated with current decoder.
     * For example, composite decoder of JSON format will expect (and parse)
     * a closing bracket in the underlying input.
     */
    public fun endStructure(descriptor: CircularSerialDescriptor)

    /**
     * Checks whether the current decoder supports strictly ordered decoding of the data
     * without calling to [decodeElementIndex].
     * If the method returns `true`, the caller might skip [decodeElementIndex] calls
     * and start invoking `decode*Element` directly, incrementing the index of the element one by one.
     * This method can be called by serializers (either generated or user-defined) as a performance optimization,
     * but there is no guarantee that the method will be ever called. Practically, it means that implementations
     * that may benefit from sequential decoding should also support a regular [decodeElementIndex]-based decoding as well.
     *
     * Example of usage:
     * ```
     * class MyPair(i: Int, d: Double)
     *
     * object MyPairSerializer : KSerializer<MyPair> {
     *     // ... other methods omitted
     *
     *    fun deserialize(decoder: Decoder): MyPair {
     *        val composite = decoder.beginStructure(descriptor)
     *        if (composite.decodeSequentially()) {
     *            val i = composite.decodeIntElement(descriptor, index = 0) // Mind the sequential indexing
     *            val d = composite.decodeIntElement(descriptor, index = 1)
     *            composite.endStructure(descriptor)
     *            return MyPair(i, d)
     *        } else {
     *            // Fallback to `decodeElementIndex` loop, refer to its documentation for details
     *        }
     *    }
     * }
     * ```
     * This example is a rough equivalent of what serialization plugin generates for serializable pair class.
     *
     * Sequential decoding is a performance optimization for formats with strictly ordered schema,
     * usually binary ones. Regular formats such as JSON or ProtoBuf cannot use this optimization,
     * because e.g. in the latter example, the same data can be represented both as
     * `{"i": 1, "d": 1.0}`"` and `{"d": 1.0, "i": 1}` (thus, unordered).
     */
    @ExperimentalCircularSerializationApi
    public fun decodeSequentially(): Boolean = false

    /**
     *  Decodes the index of the next element to be decoded.
     *  Index represents a position of the current element in the serial descriptor element that can be found
     *  with [CircularSerialDescriptor.getElementIndex].
     *
     *  If this method returns non-negative index, the caller should call one of the `decode*Element` methods
     *  with a resulting index.
     *  Apart from positive values, this method can return [DECODE_DONE] to indicate that no more elements
     *  are left or [UNKNOWN_NAME] to indicate that symbol with an unknown name was encountered.
     *
     * Example of usage:
     * ```
     * class MyPair(i: Int, d: Double)
     *
     * object MyPairSerializer : KSerializer<MyPair> {
     *     // ... other methods omitted
     *
     *    fun deserialize(decoder: Decoder): MyPair {
     *        val composite = decoder.beginStructure(descriptor)
     *        var i: Int? = null
     *        var d: Double? = null
     *        while (true) {
     *            when (val index = composite.decodeElementIndex(descriptor)) {
     *                0 -> i = composite.decodeIntElement(descriptor, 0)
     *                1 -> d = composite.decodeDoubleElement(descriptor, 1)
     *                DECODE_DONE -> break // Input is over
     *                else -> error("Unexpected index: $index)
     *            }
     *        }
     *        composite.endStructure(descriptor)
     *        require(i != null && d != null)
     *        return MyPair(i, d)
     *    }
     * }
     * ```
     * This example is a rough equivalent of what serialization plugin generates for serializable pair class.
     *
     * The need in such a loop comes from unstructured nature of most serialization formats.
     * For example, JSON for the following input `{"d": 2.0, "i": 1}`, will first read `d` key with index `1`
     * and only after `i` with the index `0`.
     *
     * A potential implementation of this method for JSON format can be the following:
     * ```
     * fun decodeElementIndex(descriptor: SerialDescriptor): Int {
     *     // Ignore arrays
     *     val nextKey: String? = myStringJsonParser.nextKey()
     *     if (nextKey == null) return DECODE_DONE
     *     return descriptor.getElementIndex(nextKey) // getElementIndex can return UNKNOWN_NAME
     * }
     * ```
     *
     * If [decodeSequentially] returns `true`, the caller might skip calling this method.
     */
    public fun decodeElementIndex(descriptor: CircularSerialDescriptor): Int

    /**
     * Method to decode collection size that may be called before the collection decoding.
     * Collection type includes [Collection], [Map] and [Array] (including primitive arrays).
     * Method can return `-1` if the size is not known in advance, though for [sequential decoding][decodeSequentially]
     * knowing precise size is a mandatory requirement.
     */
    public fun decodeCollectionSize(descriptor: CircularSerialDescriptor): Int = -1

    /**
     * Decodes a boolean value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.BOOLEAN] kind.
     */
    public fun decodeBooleanElement(descriptor: CircularSerialDescriptor, index: Int): Boolean

    /**
     * Decodes a single byte value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.BYTE] kind.
     */
    public fun decodeByteElement(descriptor: CircularSerialDescriptor, index: Int): Byte

    /**
     * Decodes a 16-bit unicode character value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.CHAR] kind.
     */
    public fun decodeCharElement(descriptor: CircularSerialDescriptor, index: Int): Char

    /**
     * Decodes a 16-bit short value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.SHORT] kind.
     */
    public fun decodeShortElement(descriptor: CircularSerialDescriptor, index: Int): Short

    /**
     * Decodes a 32-bit integer value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.INT] kind.
     */
    public fun decodeIntElement(descriptor: CircularSerialDescriptor, index: Int): Int

    /**
     * Decodes a 64-bit integer value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.LONG] kind.
     */
    public fun decodeLongElement(descriptor: CircularSerialDescriptor, index: Int): Long

    /**
     * Decodes a 32-bit IEEE 754 floating point value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.FLOAT] kind.
     */
    public fun decodeFloatElement(descriptor: CircularSerialDescriptor, index: Int): Float

    /**
     * Decodes a 64-bit IEEE 754 floating point value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.DOUBLE] kind.
     */
    public fun decodeDoubleElement(descriptor: CircularSerialDescriptor, index: Int): Double

    /**
     * Decodes a string value from the underlying input.
     * The resulting value is associated with the [descriptor] element at the given [index].
     * The element at the given index should have [PrimitiveKind.STRING] kind.
     */
    public fun decodeStringElement(descriptor: CircularSerialDescriptor, index: Int): String

    /**
     * Returns [CircularDecoder] for decoding an underlying type of a value class in an inline manner.
     * Serializable value class is described by the [child descriptor][CircularSerialDescriptor.getElementDescriptor]
     * of given [descriptor] at [index].
     *
     * Namely, for the `@Serializable @JvmInline value class MyInt(val my: Int)`,
     * and `@Serializable class MyData(val myInt: MyInt)` the following sequence is used:
     * ```
     * thisDecoder.decodeInlineElement(MyData.serializer().descriptor, 0).decodeInt()
     * ```
     *
     * This method provides an opportunity for the optimization to avoid boxing of a carried value
     * and its invocation should be equivalent to the following:
     * ```
     * thisDecoder.decodeSerializableElement(MyData.serializer.descriptor, 0, MyInt.serializer())
     * ```
     *
     * Current decoder may return any other instance of [CircularDecoder] class, depending on the provided descriptor.
     * For example, when this function is called on `Json` decoder with descriptor that has
     * `UInt.serializer().descriptor` at the given [index], the returned decoder is able
     * to decode unsigned integers.
     *
     * Note that this function returns [CircularDecoder] instead of the [CircularCompositeDecoder]
     * because value classes always have the single property.
     * Calling [CircularDecoder.beginStructure] on returned instance leads to an unspecified behavior and, in general, is prohibited.
     *
     * @see CircularDecoder.decodeInline
     * @see CircularSerialDescriptor.getElementDescriptor
     */
    public fun decodeInlineElement(descriptor: CircularSerialDescriptor, index: Int): CircularDecoder

    /**
     * Decodes value of the type [T] with the given [deserializer].
     *
     * Implementations of [CircularCompositeDecoder] may use their format-specific deserializers
     * for particular data types, e.g. handle [ByteArray] specifically if format is binary.
     *
     * If value at given [index] was already decoded with previous [decodeSerializableElement] call with the same index,
     * [previousValue] would contain a previously decoded value.
     * This parameter can be used to aggregate multiple values of the given property to the only one.
     * Implementation can safely ignore it and return a new value, effectively using 'the last one wins' strategy,
     * or apply format-specific aggregating strategies, e.g. appending scattered Protobuf lists to a single one.
     */
    public fun <T : Any?> decodeSerializableElement(descriptor: CircularSerialDescriptor, index: Int,
            deserializer: CircularDeserializationStrategy<T>, previousValue: T? = null): T

    /**
     * Decodes nullable value of the type [T] with the given [deserializer].
     *
     * If value at given [index] was already decoded with previous [decodeSerializableElement] call with the same index,
     * [previousValue] would contain a previously decoded value.
     * This parameter can be used to aggregate multiple values of the given property to the only one.
     * Implementation can safely ignore it and return a new value, efficiently using 'the last one wins' strategy,
     * or apply format-specific aggregating strategies, e.g. appending scattered Protobuf lists to a single one.
     */
    @ExperimentalCircularSerializationApi
    public fun <T : Any> decodeNullableSerializableElement(descriptor: CircularSerialDescriptor, index: Int,
            deserializer: CircularDeserializationStrategy<T?>, previousValue: T? = null): T?

}