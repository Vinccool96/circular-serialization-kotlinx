package org.cirjson.serialization.encoding

import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.CircularSerializationStrategy
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.internal.NoOpCircularEncoder

/**
 * A skeleton implementation of both [CircularEncoder] and [CircularCompositeEncoder] that can be used
 * for simple formats and for testability purpose.
 * Most of the `encode*` methods have default implementation that delegates `encodeValue(value: Any)`.
 * See [CircularEncoder] documentation for information about each particular `encode*` method.
 */
@ExperimentalCircularSerializationApi
public abstract class AbstractCircularEncoder : CircularEncoder, CircularCompositeEncoder {

    override fun beginStructure(descriptor: CircularSerialDescriptor): CircularCompositeEncoder = this

    override fun endStructure(descriptor: CircularSerialDescriptor) {}

    /**
     * Invoked before writing an element that is part of the structure to determine whether it should be encoded.
     * Element information can be obtained from the [descriptor] by the given [index].
     *
     * @return `true` if the value should be encoded, false otherwise
     */
    public open fun encodeElement(descriptor: CircularSerialDescriptor, index: Int): Boolean = true

    /**
     * Invoked to encode a value when specialized `encode*` method was not overridden.
     */
    public open fun encodeValue(value: Any): Unit = throw CircularSerializationException(
            "Non-serializable ${value::class} is not supported by ${this::class} encoder")

    override fun encodeNull() {
        throw CircularSerializationException("'null' is not supported by default")
    }

    override fun encodeBoolean(value: Boolean): Unit = encodeValue(value)

    override fun encodeByte(value: Byte): Unit = encodeValue(value)

    override fun encodeShort(value: Short): Unit = encodeValue(value)

    override fun encodeInt(value: Int): Unit = encodeValue(value)

    override fun encodeLong(value: Long): Unit = encodeValue(value)

    override fun encodeFloat(value: Float): Unit = encodeValue(value)

    override fun encodeDouble(value: Double): Unit = encodeValue(value)

    override fun encodeChar(value: Char): Unit = encodeValue(value)

    override fun encodeString(value: String): Unit = encodeValue(value)

    override fun encodeEnum(enumDescriptor: CircularSerialDescriptor, index: Int): Unit = encodeValue(index)

    override fun encodeInline(descriptor: CircularSerialDescriptor): CircularEncoder = this

    // Delegating implementation of CompositeEncoder
    final override fun encodeBooleanElement(descriptor: CircularSerialDescriptor, index: Int, value: Boolean) {
        if (encodeElement(descriptor, index)) encodeBoolean(value)
    }

    final override fun encodeByteElement(descriptor: CircularSerialDescriptor, index: Int, value: Byte) {
        if (encodeElement(descriptor, index)) encodeByte(value)
    }

    final override fun encodeShortElement(descriptor: CircularSerialDescriptor, index: Int, value: Short) {
        if (encodeElement(descriptor, index)) encodeShort(value)
    }

    final override fun encodeIntElement(descriptor: CircularSerialDescriptor, index: Int, value: Int) {
        if (encodeElement(descriptor, index)) encodeInt(value)
    }

    final override fun encodeLongElement(descriptor: CircularSerialDescriptor, index: Int, value: Long) {
        if (encodeElement(descriptor, index)) encodeLong(value)
    }

    final override fun encodeFloatElement(descriptor: CircularSerialDescriptor, index: Int, value: Float) {
        if (encodeElement(descriptor, index)) encodeFloat(value)
    }

    final override fun encodeDoubleElement(descriptor: CircularSerialDescriptor, index: Int, value: Double) {
        if (encodeElement(descriptor, index)) encodeDouble(value)
    }

    final override fun encodeCharElement(descriptor: CircularSerialDescriptor, index: Int, value: Char) {
        if (encodeElement(descriptor, index)) encodeChar(value)
    }

    final override fun encodeStringElement(descriptor: CircularSerialDescriptor, index: Int, value: String) {
        if (encodeElement(descriptor, index)) encodeString(value)
    }

    final override fun encodeInlineElement(descriptor: CircularSerialDescriptor, index: Int): CircularEncoder =
            if (encodeElement(descriptor, index)) encodeInline(
                    descriptor.getElementDescriptor(index)) else NoOpCircularEncoder

    override fun <T : Any?> encodeSerializableElement(descriptor: CircularSerialDescriptor, index: Int,
            serializer: CircularSerializationStrategy<T>, value: T) {
        if (encodeElement(descriptor, index)) encodeSerializableValue(serializer, value)
    }

    override fun <T : Any> encodeNullableSerializableElement(descriptor: CircularSerialDescriptor, index: Int,
            serializer: CircularSerializationStrategy<T>, value: T?) {
        if (encodeElement(descriptor, index)) encodeNullableSerializableValue(serializer, value)
    }

}