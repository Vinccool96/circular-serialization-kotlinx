package org.cirjson.serialization.encoding

import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

/**
 * A skeleton implementation of both [CircularDecoder] and [CircularCompositeDecoder] that can be used
 * for simple formats and for testability purpose.
 * Most of the `decode*` methods have default implementation that delegates `decodeValue(value: Any) as TargetType`.
 * See [CircularDecoder] documentation for information about each particular `decode*` method.
 */
@ExperimentalCircularSerializationApi
public abstract class AbstractCircularDecoder : CircularDecoder, CircularCompositeDecoder {

    /**
     * Invoked to decode a value when specialized `decode*` method was not overridden.
     */
    public open fun decodeValue(): Any =
            throw CircularSerializationException("${this::class} can't retrieve untyped values")

    override fun decodeNotNullMark(): Boolean = true

    override fun decodeNull(): Nothing? = null

    override fun decodeBoolean(): Boolean = decodeValue() as Boolean

    override fun decodeByte(): Byte = decodeValue() as Byte

    override fun decodeShort(): Short = decodeValue() as Short

    override fun decodeInt(): Int = decodeValue() as Int

    override fun decodeLong(): Long = decodeValue() as Long

    override fun decodeFloat(): Float = decodeValue() as Float

    override fun decodeDouble(): Double = decodeValue() as Double

    override fun decodeChar(): Char = decodeValue() as Char

    override fun decodeString(): String = decodeValue() as String

    override fun decodeEnum(enumDescriptor: CircularSerialDescriptor): Int = decodeValue() as Int

    override fun decodeInline(descriptor: CircularSerialDescriptor): CircularDecoder = this

    // overwrite by default
    public open fun <T : Any?> decodeSerializableValue(deserializer: CircularDeserializationStrategy<T>,
            previousValue: T? = null): T = decodeSerializableValue(deserializer)

    override fun beginStructure(descriptor: CircularSerialDescriptor): CircularCompositeDecoder = this

    override fun endStructure(descriptor: CircularSerialDescriptor) {
    }

    final override fun decodeBooleanElement(descriptor: CircularSerialDescriptor, index: Int): Boolean = decodeBoolean()

    final override fun decodeByteElement(descriptor: CircularSerialDescriptor, index: Int): Byte = decodeByte()

    final override fun decodeShortElement(descriptor: CircularSerialDescriptor, index: Int): Short = decodeShort()

    final override fun decodeIntElement(descriptor: CircularSerialDescriptor, index: Int): Int = decodeInt()

    final override fun decodeLongElement(descriptor: CircularSerialDescriptor, index: Int): Long = decodeLong()

    final override fun decodeFloatElement(descriptor: CircularSerialDescriptor, index: Int): Float = decodeFloat()

    final override fun decodeDoubleElement(descriptor: CircularSerialDescriptor, index: Int): Double = decodeDouble()

    final override fun decodeCharElement(descriptor: CircularSerialDescriptor, index: Int): Char = decodeChar()

    final override fun decodeStringElement(descriptor: CircularSerialDescriptor, index: Int): String = decodeString()

    override fun decodeInlineElement(descriptor: CircularSerialDescriptor, index: Int): CircularDecoder =
            decodeInline(descriptor.getElementDescriptor(index))

    override fun <T> decodeSerializableElement(descriptor: CircularSerialDescriptor, index: Int,
            deserializer: CircularDeserializationStrategy<T>, previousValue: T?): T =
            decodeSerializableValue(deserializer, previousValue)

    final override fun <T : Any> decodeNullableSerializableElement(descriptor: CircularSerialDescriptor, index: Int,
            deserializer: CircularDeserializationStrategy<T?>, previousValue: T?): T? {
        val isNullabilitySupported = deserializer.descriptor.isNullable
        return if (isNullabilitySupported || decodeNotNullMark()) decodeSerializableValue(deserializer,
                previousValue) else decodeNull()
    }

}