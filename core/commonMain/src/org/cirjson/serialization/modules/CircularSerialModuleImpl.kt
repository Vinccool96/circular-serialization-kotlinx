package org.cirjson.serialization.modules

import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationStrategy
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import kotlin.jvm.JvmField
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * A default implementation of [CircularSerializersModule]
 * which uses hash maps to store serializers associated with KClasses.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularSerialModuleImpl(private val class2ContextualFactory: Map<KClass<*>, CircularContextualProvider>,
        @JvmField val polyBase2Serializers: Map<KClass<*>, Map<KClass<*>, CircularKSerializer<*>>>,
        private val polyBase2DefaultSerializerProvider: Map<KClass<*>, PolymorphicCircularSerializerProvider<*>>,
        private val polyBase2NamedSerializers: Map<KClass<*>, Map<String, CircularKSerializer<*>>>,
        private val polyBase2DefaultDeserializerProvider: Map<KClass<*>, PolymorphicCircularDeserializerProvider<*>>) :
        CircularSerializersModule() {

    override fun <T : Any> getPolymorphic(baseClass: KClass<in T>, value: T): CircularSerializationStrategy<T>? {
        if (!baseClass.isInstance(value)) return null // Registered
        val registered = polyBase2Serializers[baseClass]?.get(value::class) as? CircularSerializationStrategy<T>
        if (registered != null) return registered // Default
        return (polyBase2DefaultSerializerProvider[baseClass] as? PolymorphicCircularSerializerProvider<T>)?.invoke(
                value)
    }

    override fun <T : Any> getPolymorphic(baseClass: KClass<in T>,
            serializedClassName: String?): CircularDeserializationStrategy<T>? { // Registered
        val registered = polyBase2NamedSerializers[baseClass]?.get(serializedClassName) as? CircularKSerializer<out T>
        if (registered != null) return registered // Default
        return (polyBase2DefaultDeserializerProvider[baseClass] as? PolymorphicCircularDeserializerProvider<T>)?.invoke(
                serializedClassName)
    }

    override fun <T : Any> getContextual(kClass: KClass<T>,
            typeArgumentsSerializers: List<CircularKSerializer<*>>): CircularKSerializer<T>? {
        return (class2ContextualFactory[kClass]?.invoke(typeArgumentsSerializers)) as? CircularKSerializer<T>?
    }

    override fun dumpTo(collector: CircularSerializersModuleCollector) {
        class2ContextualFactory.forEach { (kclass, serial) ->
            when (serial) {
                is CircularContextualProvider.Argless -> collector.contextual(kclass as KClass<Any>,
                        serial.serializer as CircularKSerializer<Any>)
                is CircularContextualProvider.WithTypeArguments -> collector.contextual(kclass, serial.provider)
            }
        }

        polyBase2Serializers.forEach { (baseClass, classMap) ->
            classMap.forEach { (actualClass, serializer) ->
                collector.polymorphic(baseClass as KClass<Any>, actualClass as KClass<Any>, serializer.cast())
            }
        }

        polyBase2DefaultSerializerProvider.forEach { (baseClass, provider) ->
            collector.polymorphicDefaultSerializer(baseClass as KClass<Any>,
                    provider as (PolymorphicCircularSerializerProvider<Any>))
        }

        polyBase2DefaultDeserializerProvider.forEach { (baseClass, provider) ->
            collector.polymorphicDefaultDeserializer(baseClass as KClass<Any>,
                    provider as (PolymorphicCircularDeserializerProvider<out Any>))
        }
    }
}

