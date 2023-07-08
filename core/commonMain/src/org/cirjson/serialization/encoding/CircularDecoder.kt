package org.cirjson.serialization.encoding

import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.descriptors.SerialKind
import org.cirjson.serialization.modules.CircularSerializersModule

/**
 * Decoder is a core deserialization primitive that encapsulates the knowledge of the underlying
 * format and an underlying storage, exposing only structural methods to the deserializer, making it completely
 * format-agnostic. Deserialization process takes a decoder and asks him for a sequence of primitive elements,
 * defined by a deserializer serial form, while decoder knows how to retrieve these primitive elements from an actual format
 * representations.
 *
 * Decoder provides high-level API that operates with basic primitive types, collections
 * and nested structures. Internally, the decoder represents input storage, and operates with its state
 * and lower level format-specific details.
 *
 * To be more specific, serialization asks a decoder for a sequence of "give me an int, give me
 * a double, give me a list of strings and give me another object that is a nested int", while decoding
 * transforms this sequence into a format-specific commands such as "parse the part of the string until the next quotation mark
 * as an int to retrieve an int, parse everything within the next curly braces to retrieve elements of a nested object etc."
 *
 * The symmetric interface for the serialization process is [CircularEncoder].
 *
 * ### Deserialization. Primitives
 *
 * If a class is represented as a single [primitive][PrimitiveKind] value in its serialized form,
 * then one of the `decode*` methods (e.g. [decodeInt]) can be used directly.
 *
 * ### Deserialization. Structured types
 *
 * If a class is represented as a structure or has multiple values in its serialized form,
 * `decode*` methods are not that helpful, because format may not require a strict order of data
 * (e.g. JSON or XML), do not allow working with collection types or establish structure boundaries.
 * All these capabilities are delegated to the [CircularCompositeDecoder] interface with a more specific API surface.
 * To denote a structure start, [beginStructure] should be used.
 * ```
 * // Denote the structure start,
 * val composite = decoder.beginStructure(descriptor)
 * // Decode all elements within the structure using 'composite'
 * ...
 * // Denote the structure end
 * composite.endStructure(descriptor)
 * ```
 *
 * E.g. if the decoder belongs to JSON format, then [beginStructure] will parse an opening bracket
 * (`{` or `[`, depending on the descriptor kind), returning the [CircularCompositeDecoder] that is aware of colon separator,
 * that should be read after each key-value pair, whilst [CircularCompositeDecoder.endStructure] will parse a closing bracket.
 *
 * ### Exception guarantees.
 * For the regular exceptions, such as invalid input, missing control symbols or attributes and unknown symbols,
 * [CircularSerializationException] can be thrown by any decoder methods. It is recommended to declare a format-specific
 * subclass of [CircularSerializationException] and throw it.
 *
 * ### Format encapsulation
 *
 * For example, for the following deserializer:
 * ```
 * class StringHolder(val stringValue: String)
 *
 * object StringPairDeserializer : DeserializationStrategy<StringHolder> {
 *    override val descriptor = ...
 *
 *    override fun deserializer(decoder: Decoder): StringHolder {
 *        // Denotes start of the structure, StringHolder is not a "plain" data type
 *        val composite = decoder.beginStructure(descriptor)
 *        if (composite.decodeElementIndex(descriptor) != 0)
 *            throw MissingFieldException("Field 'stringValue' is missing")
 *        // Decode the nested string value
 *        val value = composite.decodeStringElement(descriptor, index = 0)
 *        // Denotes end of the structure
 *        composite.endStructure(descriptor)
 *    }
 * }
 * ```
 *
 * ### Exception safety
 *
 * In general, catching [CircularSerializationException] from any of `decode*` methods is not allowed and produces unspecified behaviour.
 * After thrown exception, current decoder is left in an arbitrary state, no longer suitable for further decoding.
 *
 * This deserializer does not know anything about the underlying data and will work with any properly-implemented decoder.
 * JSON, for example, parses an opening bracket `{` during the `beginStructure` call, checks that the next key
 * after this bracket is `stringValue` (using the descriptor), returns the value after the colon as string value
 * and parses closing bracket `}` during the `endStructure`.
 * XML would do roughly the same, but with different separators and parsing structures, while ProtoBuf
 * machinery could be completely different.
 * In any case, all these parsing details are encapsulated by a decoder.
 *
 * ### Decoder implementation
 *
 * While being strictly typed, an underlying format can transform actual types in the way it wants.
 * For example, a format can support only string types and encode/decode all primitives in a string form:
 * ```
 * StringFormatDecoder : Decoder {
 *
 *     ...
 *     override fun decodeDouble(): Double = decodeString().toDouble()
 *     override fun decodeInt(): Int = decodeString().toInt()
 *     ...
 * }
 * ```
 *
 * ### Not stable for inheritance
 *
 * `Decoder` interface is not stable for inheritance in 3rd-party libraries, as new methods
 * might be added to this interface or contracts of the existing methods can be changed.
 */
public interface CircularDecoder {

    /**
     * Context of the current serialization process, including contextual and polymorphic serialization and,
     * potentially, a format-specific configuration.
     */
    public val serializersModule: CircularSerializersModule

    /**
     * Returns `true` if the current value in decoder is not null, false otherwise.
     * This method is usually used to decode potentially nullable data:
     * ```
     * // Could be String? deserialize() method
     * public fun deserialize(decoder: Decoder): String? {
     *     if (decoder.decodeNotNullMark()) {
     *         return decoder.decodeString()
     *     } else {
     *         return decoder.decodeNull()
     *     }
     * }
     * ```
     */
    @ExperimentalCircularSerializationApi
    public fun decodeNotNullMark(): Boolean

    /**
     * Decodes the `null` value and returns it.
     *
     * It is expected that `decodeNotNullMark` was called
     * prior to `decodeNull` invocation and the case when it returned `true` was handled.
     */
    @ExperimentalCircularSerializationApi
    public fun decodeNull(): Nothing?

    /**
     * Decodes a boolean value.
     * Corresponding kind is [PrimitiveKind.BOOLEAN].
     */
    public fun decodeBoolean(): Boolean

    /**
     * Decodes a single byte value.
     * Corresponding kind is [PrimitiveKind.BYTE].
     */
    public fun decodeByte(): Byte

    /**
     * Decodes a 16-bit short value.
     * Corresponding kind is [PrimitiveKind.SHORT].
     */
    public fun decodeShort(): Short

    /**
     * Decodes a 16-bit unicode character value.
     * Corresponding kind is [PrimitiveKind.CHAR].
     */
    public fun decodeChar(): Char

    /**
     * Decodes a 32-bit integer value.
     * Corresponding kind is [PrimitiveKind.INT].
     */
    public fun decodeInt(): Int

    /**
     * Decodes a 64-bit integer value.
     * Corresponding kind is [PrimitiveKind.LONG].
     */
    public fun decodeLong(): Long

    /**
     * Decodes a 32-bit IEEE 754 floating point value.
     * Corresponding kind is [PrimitiveKind.FLOAT].
     */
    public fun decodeFloat(): Float

    /**
     * Decodes a 64-bit IEEE 754 floating point value.
     * Corresponding kind is [PrimitiveKind.DOUBLE].
     */
    public fun decodeDouble(): Double

    /**
     * Decodes a string value.
     * Corresponding kind is [PrimitiveKind.STRING].
     */
    public fun decodeString(): String

    /**
     * Decodes a enum value and returns its index in [enumDescriptor] elements collection.
     * Corresponding kind is [SerialKind.ENUM].
     *
     * E.g. for the enum `enum class Letters { A, B, C, D }` and
     * underlying input "C", [decodeEnum] method should return `2` as a result.
     *
     * This method does not imply any restrictions on the input format,
     * the format is free to store the enum by its name, index, ordinal or any other enum representation.
     */
    public fun decodeEnum(enumDescriptor: CircularSerialDescriptor): Int

    /**
     * Returns [CircularDecoder] for decoding an underlying type of a value class in an inline manner.
     * [descriptor] describes a target value class.
     *
     * Namely, for the `@Serializable @JvmInline value class MyInt(val my: Int)`, the following sequence is used:
     * ```
     * thisDecoder.decodeInline(MyInt.serializer().descriptor).decodeInt()
     * ```
     *
     * Current decoder may return any other instance of [CircularDecoder] class, depending on the provided [descriptor].
     * For example, when this function is called on `Json` decoder with
     * `UInt.serializer().descriptor`, the returned decoder is able to decode unsigned integers.
     *
     * Note that this function returns [CircularDecoder] instead of the [CircularCompositeDecoder]
     * because value classes always have the single property.
     *
     * Calling [CircularDecoder.beginStructure] on returned instance leads to an unspecified behavior and, in general, is prohibited.
     */
    public fun decodeInline(descriptor: CircularSerialDescriptor): CircularDecoder

    /**
     * Decodes the beginning of the nested structure in a serialized form
     * and returns [CircularCompositeDecoder] responsible for decoding this very structure.
     *
     * Typically, classes, collections and maps are represented as a nested structure in a serialized form.
     * E.g. the following JSON
     * ```
     * {
     *     "a": 2,
     *     "b": { "nested": "c" }
     *     "c": [1, 2, 3],
     *     "d": null
     * }
     * ```
     * has three nested structures: the very beginning of the data, "b" value and "c" value.
     */
    public fun beginStructure(descriptor: CircularSerialDescriptor): CircularCompositeDecoder

    /**
     * Decodes the value of type [T] by delegating the decoding process to the given [deserializer].
     * For example, `decodeInt` call us equivalent to delegating integer decoding to [Int.serializer][Int.Companion.serializer]:
     * `decodeSerializableValue(IntSerializer)`
     */
    public fun <T : Any?> decodeSerializableValue(deserializer: CircularDeserializationStrategy<T>): T =
        deserializer.deserialize(this)

    /**
     * Decodes the nullable value of type [T] by delegating the decoding process to the given [deserializer].
     */
    @ExperimentalCircularSerializationApi
    public fun <T : Any> decodeNullableSerializableValue(deserializer: CircularDeserializationStrategy<T?>): T? {
        val isNullabilitySupported = deserializer.descriptor.isNullable
        return if (isNullabilitySupported || decodeNotNullMark()) decodeSerializableValue(deserializer) else decodeNull()
    }

}