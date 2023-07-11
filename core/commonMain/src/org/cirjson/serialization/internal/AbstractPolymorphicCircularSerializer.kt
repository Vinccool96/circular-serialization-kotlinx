package org.cirjson.serialization.internal

import org.cirjson.serialization.*
import org.cirjson.serialization.encoding.*
import kotlin.reflect.KClass

/**
 * Base class for providing multiplatform polymorphic serialization.
 *
 * This class cannot be implemented by library users. To learn how to use it for your case,
 * please refer to [PolymorphicSerializer] for interfaces/abstract classes and [SealedClassSerializer] for sealed classes.
 *
 * By default, without special support from [Encoder], polymorphic types are serialized as list with
 * two elements: class [serial name][SerialDescriptor.serialName] (String) and the object itself.
 * Serial name equals to fully-qualified class name by default and can be changed via @[SerialName] annotation.
 */
@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
public abstract class AbstractPolymorphicCircularSerializer<T : Any> internal constructor() : CircularKSerializer<T> {

    /**
     * Base class for all classes that this polymorphic serializer can serialize or deserialize.
     */
    public abstract val baseClass: KClass<T>

    public final override fun serialize(encoder: CircularEncoder, value: T) {
        val actualSerializer = findPolymorphicSerializer(encoder, value)
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, actualSerializer.descriptor.serialName)
            encodeSerializableElement(descriptor, 1, actualSerializer.cast(), value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    public final override fun deserialize(decoder: CircularDecoder): T = decoder.decodeStructure(descriptor) {
        var klassName: String? = null
        var value: Any? = null
        if (decodeSequentially()) {
            return@decodeStructure decodeSequentially(this)
        }

        mainLoop@ while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                CircularCompositeDecoder.DECODE_DONE -> {
                    break@mainLoop
                }
                0 -> {
                    klassName = decodeStringElement(descriptor, index)
                }
                1 -> {
                    klassName = requireNotNull(klassName) { "Cannot read polymorphic value before its type token" }
                    val serializer = findPolymorphicSerializer(this, klassName)
                    value = decodeSerializableElement(descriptor, index, serializer)
                }
                else -> throw CircularSerializationException(
                        "Invalid index in polymorphic deserialization of " + (klassName
                                ?: "unknown class") + "\n Expected 0, 1 or DECODE_DONE(-1), but found $index")
            }
        }
        requireNotNull(value) { "Polymorphic value has not been read for class $klassName" } as T
    }

    private fun decodeSequentially(compositeDecoder: CircularCompositeDecoder): T {
        val klassName = compositeDecoder.decodeStringElement(descriptor, 0)
        val serializer = findPolymorphicSerializer(compositeDecoder, klassName)
        return compositeDecoder.decodeSerializableElement(descriptor, 1, serializer)
    }

    /**
     * Lookups an actual serializer for given [klassName] withing the current [base class][baseClass].
     * May use context from the [decoder].
     */
    @InternalCircularSerializationApi
    public open fun findPolymorphicSerializerOrNull(decoder: CircularCompositeDecoder,
            klassName: String?): CircularDeserializationStrategy<T>? =
            decoder.serializersModule.getPolymorphic(baseClass, klassName)

    /**
     * Lookups an actual serializer for given [value] within the current [base class][baseClass].
     * May use context from the [encoder].
     */
    @InternalCircularSerializationApi
    public open fun findPolymorphicSerializerOrNull(encoder: CircularEncoder,
            value: T): CircularSerializationStrategy<T>? = encoder.serializersModule.getPolymorphic(baseClass, value)

}