package org.cirjson.serialization.modules

import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationStrategy
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import kotlin.reflect.KClass

/**
 * [CircularSerializersModuleCollector] can introspect and accumulate content of any [CircularSerializersModule] via [CircularSerializersModule.dumpTo],
 * using a visitor-like pattern: [contextual] and [polymorphic] functions are invoked for each registered serializer.
 *
 * ### Not stable for inheritance
 *
 * `SerializersModuleCollector` interface is not stable for inheritance in 3rd party libraries, as new methods
 * might be added to this interface or contracts of the existing methods can be changed.
 */
@ExperimentalCircularSerializationApi
public interface CircularSerializersModuleCollector {

    /**
     * Accept a serializer, associated with [kClass] for contextual serialization.
     */
    public fun <T : Any> contextual(kClass: KClass<T>, serializer: CircularKSerializer<T>): Unit =
            contextual(kClass) { serializer }

    /**
     * Accept a provider, associated with generic [kClass] for contextual serialization.
     */
    public fun <T : Any> contextual(kClass: KClass<T>,
            provider: (typeArgumentsSerializers: List<CircularKSerializer<*>>) -> CircularKSerializer<*>)

    /**
     * Accept a serializer, associated with [actualClass] for polymorphic serialization.
     */
    public fun <Base : Any, Sub : Base> polymorphic(baseClass: KClass<Base>, actualClass: KClass<Sub>,
            actualSerializer: CircularKSerializer<Sub>)

    /**
     * Accept a default serializer provider, associated with the [baseClass] for polymorphic serialization.
     * [defaultSerializerProvider] is invoked when no polymorphic serializers for `value` in the scope of [baseClass] were found.
     *
     * Default serializers provider affects only serialization process. Deserializers are accepted in the
     * [CircularSerializersModuleCollector.polymorphicDefaultDeserializer] method.
     *
     * [defaultSerializerProvider] can be stateful and lookup a serializer for the missing type dynamically.
     */
    public fun <Base : Any> polymorphicDefaultSerializer(baseClass: KClass<Base>,
            defaultSerializerProvider: (value: Base) -> CircularSerializationStrategy<Base>?)

    /**
     * Accept a default deserializer provider, associated with the [baseClass] for polymorphic deserialization.
     * [defaultDeserializerProvider] is invoked when no polymorphic serializers associated with the `className`
     * in the scope of [baseClass] were found. `className` could be `null` for formats that support nullable class discriminators
     * (currently only `Json` with `useArrayPolymorphism` set to `false`).
     *
     * Default deserializers provider affects only deserialization process. Serializers are accepted in the
     * [CircularSerializersModuleCollector.polymorphicDefaultSerializer] method.
     *
     * [defaultDeserializerProvider] can be stateful and lookup a serializer for the missing type dynamically.
     */
    public fun <Base : Any> polymorphicDefaultDeserializer(baseClass: KClass<Base>,
            defaultDeserializerProvider: (className: String?) -> CircularDeserializationStrategy<Base>?)

    /**
     * Accept a default deserializer provider, associated with the [baseClass] for polymorphic deserialization.
     *
     * This function affect only deserialization process. To avoid confusion, it was deprecated and replaced with [polymorphicDefaultDeserializer].
     * To affect serialization process, use [CircularSerializersModuleCollector.polymorphicDefaultSerializer].
     *
     * [defaultDeserializerProvider] is invoked when no polymorphic serializers associated with the `className`
     * in the scope of [baseClass] were found. `className` could be `null` for formats that support nullable class discriminators
     * (currently only `Json` with `useArrayPolymorphism` set to `false`).
     *
     * [defaultDeserializerProvider] can be stateful and lookup a serializer for the missing type dynamically.
     *
     * @see CircularSerializersModuleCollector.polymorphicDefaultDeserializer
     * @see CircularSerializersModuleCollector.polymorphicDefaultSerializer
     */
    @Deprecated("Deprecated in favor of function with more precise name: polymorphicDefaultDeserializer",
            ReplaceWith("polymorphicDefaultDeserializer(baseClass, defaultDeserializerProvider)"),
            DeprecationLevel.WARNING // Since 1.5.0. Raise to ERROR in 1.6.0, hide in 1.7.0
    )
    public fun <Base : Any> polymorphicDefault(baseClass: KClass<Base>,
            defaultDeserializerProvider: (className: String?) -> CircularDeserializationStrategy<Base>?) {
        polymorphicDefaultDeserializer(baseClass, defaultDeserializerProvider)
    }

}