package org.cirjson.serialization.encoding

import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.CircularSerializationStrategy
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.descriptors.SerialKind
import org.cirjson.serialization.modules.CircularSerializersModule

/**
 * Encoder is a core serialization primitive that encapsulates the knowledge of the underlying
 * format and its storage, exposing only structural methods to the serializer, making it completely
 * format-agnostic. Serialization process transforms a single value into the sequence of its
 * primitive elements, also called its serial form, while encoding transforms these primitive elements into an actual
 * format representation: JSON string, ProtoBuf ByteArray, in-memory map representation etc.
 *
 * Encoder provides high-level API that operates with basic primitive types, collections
 * and nested structures. Internally, encoder represents output storage and operates with its state
 * and lower level format-specific details.
 *
 * To be more specific, serialization transforms a value into a sequence of "here is an int, here is
 * a double, here a list of strings and here is another object that is a nested int", while encoding
 * transforms this sequence into a format-specific commands such as "insert opening curly bracket
 * for a nested object start, insert a name of the value, and the value separated with colon for an int etc."
 *
 * The symmetric interface for the deserialization process is [CircularDecoder].
 *
 * ### Serialization. Primitives
 *
 * If a class is represented as a single [primitive][PrimitiveKind] value in its serialized form,
 * then one of the `encode*` methods (e.g. [encodeInt]) can be used directly.
 *
 * ### Serialization. Structured types.
 *
 * If a class is represented as a structure or has multiple values in its serialized form,
 * `encode*` methods are not that helpful, because they do not allow working with collection types or establish structure boundaries.
 * All these capabilities are delegated to the [CircularCompositeEncoder] interface with a more specific API surface.
 * To denote a structure start, [beginStructure] should be used.
 * ```
 * // Denote the structure start,
 * val composite = encoder.beginStructure(descriptor)
 * // Encoding all elements within the structure using 'composite'
 * ...
 * // Denote the structure end
 * composite.endStructure(descriptor)
 * ```
 *
 * E.g. if the encoder belongs to JSON format, then [beginStructure] will write an opening bracket
 * (`{` or `[`, depending on the descriptor kind), returning the [CircularCompositeEncoder] that is aware of colon separator,
 * that should be appended between each key-value pair, whilst [CircularCompositeEncoder.endStructure] will write a closing bracket.
 *
 * ### Exception guarantees.
 * For the regular exceptions, such as invalid input, conflicting serial names,
 * [CircularSerializationException] can be thrown by any encoder methods.
 * It is recommended to declare a format-specific subclass of [CircularSerializationException] and throw it.
 *
 * ### Format encapsulation
 *
 * For example, for the following serializer:
 * ```
 * class StringHolder(val stringValue: String)
 *
 * object StringPairDeserializer : SerializationStrategy<StringHolder> {
 *    override val descriptor = ...
 *
 *    override fun serializer(encoder: Encoder, value: StringHolder) {
 *        // Denotes start of the structure, StringHolder is not a "plain" data type
 *        val composite = encoder.beginStructure(descriptor)
 *        // Encode the nested string value
 *        composite.encodeStringElement(descriptor, index = 0)
 *        // Denotes end of the structure
 *        composite.endStructure(descriptor)
 *    }
 * }
 * ```
 *
 * This serializer does not know anything about the underlying storage and will work with any properly-implemented encoder.
 * JSON, for example, writes an opening bracket `{` during the `beginStructure` call, writes 'stringValue` key along
 * with its value in `encodeStringElement` and writes the closing bracket `}` during the `endStructure`.
 * XML would do roughly the same, but with different separators and structures, while ProtoBuf
 * machinery could be completely different.
 * In any case, all these parsing details are encapsulated by an encoder.
 *
 * ### Exception safety
 *
 * In general, catching [CircularSerializationException] from any of `encode*` methods is not allowed and produces unspecified behaviour.
 * After thrown exception, current encoder is left in an arbitrary state, no longer suitable for further encoding.
 *
 * ### Encoder implementation.
 *
 * While being strictly typed, an underlying format can transform actual types in the way it wants.
 * For example, a format can support only string types and encode/decode all primitives in a string form:
 * ```
 * StringFormatEncoder : Encoder {
 *
 *     ...
 *     override fun encodeDouble(value: Double) = encodeString(value.toString())
 *     override fun encodeInt(value: Int) = encodeString(value.toString())
 *     ...
 * }
 * ```
 *
 * ### Not stable for inheritance
 *
 * `Encoder` interface is not stable for inheritance in 3rd party libraries, as new methods
 * might be added to this interface or contracts of the existing methods can be changed.
 */
public interface CircularEncoder {

    /**
     * Context of the current serialization process, including contextual and polymorphic serialization and,
     * potentially, a format-specific configuration.
     */
    public val serializersModule: CircularSerializersModule

    /**
     * Notifies the encoder that value of a nullable type that is
     * being serialized is not null. It should be called before writing a non-null value
     * of nullable type:
     * ```
     * // Could be String? serialize method
     * if (value != null) {
     *     encoder.encodeNotNullMark()
     *     encoder.encodeStringValue(value)
     * } else {
     *     encoder.encodeNull()
     * }
     * ```
     *
     * This method has a use in highly-performant binary formats and can
     * be safely ignore by most of the regular formats.
     */
    @ExperimentalCircularSerializationApi
    public fun encodeNotNullMark() {
    }

    /**
     * Encodes `null` value.
     */
    @ExperimentalCircularSerializationApi
    public fun encodeNull()

    /**
     * Encodes a boolean value.
     * Corresponding kind is [PrimitiveKind.BOOLEAN].
     */
    public fun encodeBoolean(value: Boolean)

    /**
     * Encodes a single byte value.
     * Corresponding kind is [PrimitiveKind.BYTE].
     */
    public fun encodeByte(value: Byte)

    /**
     * Encodes a 16-bit short value.
     * Corresponding kind is [PrimitiveKind.SHORT].
     */
    public fun encodeShort(value: Short)

    /**
     * Encodes a 16-bit unicode character value.
     * Corresponding kind is [PrimitiveKind.CHAR].
     */
    public fun encodeChar(value: Char)

    /**
     * Encodes a 32-bit integer value.
     * Corresponding kind is [PrimitiveKind.INT].
     */
    public fun encodeInt(value: Int)

    /**
     * Encodes a 64-bit integer value.
     * Corresponding kind is [PrimitiveKind.LONG].
     */
    public fun encodeLong(value: Long)

    /**
     * Encodes a 32-bit IEEE 754 floating point value.
     * Corresponding kind is [PrimitiveKind.FLOAT].
     */
    public fun encodeFloat(value: Float)

    /**
     * Encodes a 64-bit IEEE 754 floating point value.
     * Corresponding kind is [PrimitiveKind.DOUBLE].
     */
    public fun encodeDouble(value: Double)

    /**
     * Encodes a string value.
     * Corresponding kind is [PrimitiveKind.STRING].
     */
    public fun encodeString(value: String)

    /**
     * Encodes a enum value that is stored at the [index] in [enumDescriptor] elements collection.
     * Corresponding kind is [SerialKind.ENUM].
     *
     * E.g. for the enum `enum class Letters { A, B, C, D }` and
     * serializable value "C", [encodeEnum] method should be called with `2` as am index.
     *
     * This method does not imply any restrictions on the output format,
     * the format is free to store the enum by its name, index, ordinal or any other
     */
    public fun encodeEnum(enumDescriptor: CircularSerialDescriptor, index: Int)

    /**
     * Returns [CircularEncoder] for encoding an underlying type of a value class in an inline manner.
     * [descriptor] describes a serializable value class.
     *
     * Namely, for the `@Serializable @JvmInline value class MyInt(val my: Int)`,
     * the following sequence is used:
     * ```
     * thisEncoder.encodeInline(MyInt.serializer().descriptor).encodeInt(my)
     * ```
     *
     * Current encoder may return any other instance of [CircularEncoder] class, depending on the provided [descriptor].
     * For example, when this function is called on Json encoder with `UInt.serializer().descriptor`, the returned encoder is able
     * to encode unsigned integers.
     *
     * Note that this function returns [CircularEncoder] instead of the [CircularCompositeEncoder]
     * because value classes always have the single property.
     * Calling [CircularEncoder.beginStructure] on returned instance leads to an unspecified behavior and, in general, is prohibited.
     */
    public fun encodeInline(descriptor: CircularSerialDescriptor): CircularEncoder

    /**
     * Encodes the beginning of the nested structure in a serialized form
     * and returns [CircularCompositeDecoder] responsible for encoding this very structure.
     * E.g the hierarchy:
     * ```
     * class StringHolder(val stringValue: String)
     * class Holder(val stringHolder: StringHolder)
     * ```
     *
     * with the following serialized form in JSON:
     * ```
     * {
     *   "stringHolder" : { "stringValue": "value" }
     * }
     * ```
     *
     * will be roughly represented as the following sequence of calls:
     * ```
     * // Holder serializer
     * fun serialize(encoder: Encoder, value: Holder) {
     *     val composite = encoder.beginStructure(descriptor) // the very first opening bracket '{'
     *     composite.encodeSerializableElement(descriptor, 0, value.stringHolder) // Serialize nested StringHolder
     *     composite.endStructure(descriptor) // The very last closing bracket
     * }
     *
     * // StringHolder serializer
     * fun serialize(encoder: Encoder, value: StringHolder) {
     *     val composite = encoder.beginStructure(descriptor) // One more '{' when the key "stringHolder" is already written
     *     composite.encodeStringElement(descriptor, 0, value.stringValue) // Serialize actual value
     *     composite.endStructure(descriptor) // Closing bracket
     * }
     * ```
     */
    public fun beginStructure(descriptor: CircularSerialDescriptor): CircularCompositeEncoder

    /**
     * Encodes the beginning of the collection with size [collectionSize] and the given serializer of its type parameters.
     * This method has to be implemented only if you need to know collection size in advance, otherwise, [beginStructure] can be used.
     */
    public fun beginCollection(descriptor: CircularSerialDescriptor, collectionSize: Int): CircularCompositeEncoder =
            beginStructure(descriptor)

    /**
     * Encodes the [value] of type [T] by delegating the encoding process to the given [serializer].
     * For example, `encodeInt` call us equivalent to delegating integer encoding to [Int.serializer][Int.Companion.serializer]:
     * `encodeSerializableValue(Int.serializer())`
     */
    public fun <T : Any?> encodeSerializableValue(serializer: CircularSerializationStrategy<T>, value: T) {
        serializer.serialize(this, value)
    }

    /**
     * Encodes the nullable [value] of type [T] by delegating the encoding process to the given [serializer].
     */
    @Suppress("UNCHECKED_CAST")
    @ExperimentalCircularSerializationApi
    public fun <T : Any> encodeNullableSerializableValue(serializer: CircularSerializationStrategy<T>, value: T?) {
        val isNullabilitySupported = serializer.descriptor.isNullable
        if (isNullabilitySupported) {
            // Instead of `serializer.serialize` to be able to intercept this
            return encodeSerializableValue(serializer as CircularSerializationStrategy<T?>, value)
        }

        // Else default path used to avoid allocation of NullableSerializer
        if (value == null) {
            encodeNull()
        } else {
            encodeNotNullMark()
            encodeSerializableValue(serializer, value)
        }
    }

}