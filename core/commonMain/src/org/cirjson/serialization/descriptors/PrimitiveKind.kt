package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.PrimitiveKind.INT
import org.cirjson.serialization.descriptors.StructureKind.*

/**
 * Values of primitive kinds usually are represented as a single value.
 * All default serializers for Kotlin [primitives types](https://kotlinlang.org/docs/tutorials/kotlin-for-py/primitive-data-types-and-their-limitations.html)
 * and [String] have primitive kind.
 *
 * ### Serializers interaction
 *
 * Serialization formats typically handle these kinds by calling a corresponding primitive method on encoder or decoder.
 * For example, if the following serializable class `class Color(val red: Byte, val green: Byte, val blue: Byte)` is represented by your serializer
 * as a single [Int] value, a typical serializer will serialize its value in the following manner:
 * ```
 * val intValue = color.rgbToInt()
 * encoder.encodeInt(intValue)
 * ```
 * and a corresponding [Decoder] counterpart.
 *
 * ### Implementation note
 *
 * Serial descriptors for primitive kinds are not expected to have any nested elements, thus its element count should be zero.
 * If a class is represented as a primitive value, its corresponding serial name *should not* be equal to the corresponding primitive type name.
 * For the `Color` example, represented as single [Int], its descriptor should have [INT] kind, zero elements and serial name **not equals**
 * to `kotlin.Int`: `PrimitiveDescriptor("my.package.ColorAsInt", PrimitiveKind.INT)`
 */
@OptIn(ExperimentalCircularSerializationApi::class) // May be @Experimental, but break clients + makes impossible to use stable PrimitiveSerialDescriptor
public sealed class PrimitiveKind : SerialKind() {

    /**
     * Primitive kind that represents a boolean `true`/`false` value.
     * Corresponding Kotlin primitive is [Boolean].
     * Corresponding encoder and decoder methods are [Encoder.encodeBoolean] and [Decoder.decodeBoolean].
     */
    public object BOOLEAN : PrimitiveKind()

    /**
     * Primitive kind that represents a single byte value.
     * Corresponding Kotlin primitive is [Byte].
     * Corresponding encoder and decoder methods are [Encoder.encodeByte] and [Decoder.decodeByte].
     */
    public object BYTE : PrimitiveKind()

    /**
     * Primitive kind that represents a 16-bit unicode character value.
     * Corresponding Kotlin primitive is [Char].
     * Corresponding encoder and decoder methods are [Encoder.encodeChar] and [Decoder.decodeChar].
     */
    public object CHAR : PrimitiveKind()

    /**
     * Primitive kind that represents a 16-bit short value.
     * Corresponding Kotlin primitive is [Short].
     * Corresponding encoder and decoder methods are [Encoder.encodeShort] and [Decoder.decodeShort].
     */
    public object SHORT : PrimitiveKind()

    /**
     * Primitive kind that represents a 32-bit int value.
     * Corresponding Kotlin primitive is [Int].
     * Corresponding encoder and decoder methods are [Encoder.encodeInt] and [Decoder.decodeInt].
     */
    public object INT : PrimitiveKind()

    /**
     * Primitive kind that represents a 64-bit long value.
     * Corresponding Kotlin primitive is [Long].
     * Corresponding encoder and decoder methods are [Encoder.encodeLong] and [Decoder.decodeLong].
     */
    public object LONG : PrimitiveKind()

    /**
     * Primitive kind that represents a 32-bit IEEE 754 floating point value.
     * Corresponding Kotlin primitive is [Float].
     * Corresponding encoder and decoder methods are [Encoder.encodeFloat] and [Decoder.decodeFloat].
     */
    public object FLOAT : PrimitiveKind()

    /**
     * Primitive kind that represents a 64-bit IEEE 754 floating point value.
     * Corresponding Kotlin primitive is [Double].
     * Corresponding encoder and decoder methods are [Encoder.encodeDouble] and [Decoder.decodeDouble].
     */
    public object DOUBLE : PrimitiveKind()

    /**
     * Primitive kind that represents a string value.
     * Corresponding Kotlin primitive is [String].
     * Corresponding encoder and decoder methods are [Encoder.encodeString] and [Decoder.decodeString].
     */
    public object STRING : PrimitiveKind()

}