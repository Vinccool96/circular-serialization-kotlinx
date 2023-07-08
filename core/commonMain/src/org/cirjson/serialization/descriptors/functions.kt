package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.modules.CircularSerialModuleImpl
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.modules.CircularSerializersModuleBuilder
import org.cirjson.serialization.modules.PolymorphicCircularModuleBuilder
import kotlin.reflect.KClass

/**
 * Looks up a descriptor of serializer registered for contextual serialization in [this],
 * using [CircularSerialDescriptor.capturedKClass] as a key.
 *
 * @see CircularSerializersModuleBuilder.contextual
 */
@ExperimentalCircularSerializationApi
public fun CircularSerializersModule.getContextualDescriptor(descriptor: CircularSerialDescriptor): CircularSerialDescriptor? =
        descriptor.capturedKClass?.let { klass -> getContextual(klass)?.descriptor }

/**
 * Retrieves a collection of descriptors which serializers are registered for polymorphic serialization in [this]
 * with base class equal to [descriptor]'s [CircularSerialDescriptor.capturedKClass].
 * This method does not retrieve serializers registered with [PolymorphicCircularModuleBuilder.defaultDeserializer]
 * or [PolymorphicCircularModuleBuilder.defaultSerializer].
 *
 * @see CircularSerializersModule.getPolymorphic
 * @see CircularSerializersModuleBuilder.polymorphic
 */
@ExperimentalCircularSerializationApi
public fun CircularSerializersModule.getPolymorphicDescriptors(descriptor: CircularSerialDescriptor): List<CircularSerialDescriptor> {
    val kClass = descriptor.capturedKClass ?: return emptyList()
    // SerializersModule is sealed class with the only implementation
    return (this as CircularSerialModuleImpl).polyBase2Serializers[kClass]?.values.orEmpty().map { it.descriptor }
}

/**
 * Wraps [this] in [CircularContextDescriptor].
 */
internal fun CircularSerialDescriptor.withContext(context: KClass<*>): CircularSerialDescriptor =
        CircularContextDescriptor(this, context)