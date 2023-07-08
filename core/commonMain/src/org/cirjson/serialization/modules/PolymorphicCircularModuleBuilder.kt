package org.cirjson.serialization.modules

import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationStrategy
import kotlin.reflect.KClass

/**
 * A builder which registers all its content for polymorphic serialization in the scope of the [base class][baseClass].
 * If [baseSerializer] is present, registers it as a serializer for [baseClass] (which will be used if base class is serializable).
 * Subclasses and its serializers can be added with [subclass] builder function.
 *
 * To obtain an instance of this builder, use [CircularSerializersModuleBuilder.polymorphic] DSL function.
 */
public class PolymorphicCircularModuleBuilder<in Base : Any> @PublishedApi internal constructor(
        private val baseClass: KClass<Base>, private val baseSerializer: CircularKSerializer<Base>? = null) {

    private val subclasses: MutableList<Pair<KClass<out Base>, CircularKSerializer<out Base>>> = mutableListOf()

    private var defaultSerializerProvider: ((Base) -> CircularSerializationStrategy<Base>?)? = null

    private var defaultDeserializerProvider: ((String?) -> CircularDeserializationStrategy<Base>?)? = null

    /**
     * Registers a [subclass] [serializer] in the resulting module under the [base class][Base].
     */
    public fun <T : Base> subclass(subclass: KClass<T>, serializer: CircularKSerializer<T>) {
        subclasses.add(subclass to serializer)
    }

    /**
     * Adds a default serializers provider associated with the given [baseClass] to the resulting module.
     * [defaultDeserializerProvider] is invoked when no polymorphic serializers associated with the `className`
     * were found. `className` could be `null` for formats that support nullable class discriminators
     * (currently only `Json` with `JsonBuilder.useArrayPolymorphism` set to `false`)
     *
     * Default deserializers provider affects only deserialization process. To affect serialization process, use
     * [CircularSerializersModuleBuilder.polymorphicDefaultSerializer].
     *
     * [defaultDeserializerProvider] can be stateful and lookup a serializer for the missing type dynamically.
     *
     * Typically, if the class is not registered in advance, it is not possible to know the structure of the unknown
     * type and have a precise serializer, so the default serializer has limited capabilities.
     * If you're using `Json` format, you can get a structural access to the unknown data using `JsonContentPolymorphicSerializer`.
     *
     * @see CircularSerializersModuleBuilder.polymorphicDefaultSerializer
     */
    public fun defaultDeserializer(
            defaultDeserializerProvider: (className: String?) -> CircularDeserializationStrategy<Base>?) {
        require(this.defaultDeserializerProvider == null) {
            "Default deserializer provider is already registered for class $baseClass: ${this.defaultDeserializerProvider}"
        }
        this.defaultDeserializerProvider = defaultDeserializerProvider
    }

    /**
     * Adds a default deserializers provider associated with the given [baseClass] to the resulting module.
     * This function affect only deserialization process. To avoid confusion, it was deprecated and replaced with [defaultDeserializer].
     * To affect serialization process, use [CircularSerializersModuleBuilder.polymorphicDefaultSerializer].
     *
     * [defaultSerializerProvider] is invoked when no polymorphic serializers associated with the `className`
     * were found. `className` could be `null` for formats that support nullable class discriminators
     * (currently only `Json` with `JsonBuilder.useArrayPolymorphism` set to `false`)
     *
     * [defaultSerializerProvider] can be stateful and lookup a serializer for the missing type dynamically.
     *
     * Typically, if the class is not registered in advance, it is not possible to know the structure of the unknown
     * type and have a precise serializer, so the default serializer has limited capabilities.
     * If you're using `Json` format, you can get a structural access to the unknown data using `JsonContentPolymorphicSerializer`.
     *
     * @see defaultDeserializer
     * @see CircularSerializersModuleBuilder.polymorphicDefaultSerializer
     */
    @Deprecated("Deprecated in favor of function with more precise name: defaultDeserializer",
            ReplaceWith("defaultDeserializer(defaultSerializerProvider)"),
            DeprecationLevel.WARNING // Since 1.5.0. Raise to ERROR in 1.6.0, hide in 1.7.0
    )
    public fun default(defaultSerializerProvider: (className: String?) -> CircularDeserializationStrategy<Base>?) {
        defaultDeserializer(defaultSerializerProvider)
    }

    @Suppress("UNCHECKED_CAST")
    @PublishedApi
    internal fun buildTo(builder: CircularSerializersModuleBuilder) {
        if (baseSerializer != null) builder.registerPolymorphicSerializer(baseClass, baseClass, baseSerializer)
        subclasses.forEach { (kclass, serializer) ->
            builder.registerPolymorphicSerializer(baseClass, kclass as KClass<Base>, serializer.cast())
        }

        val defaultSerializer = defaultSerializerProvider
        if (defaultSerializer != null) {
            builder.registerDefaultPolymorphicSerializer(baseClass, defaultSerializer, false)
        }

        val defaultDeserializer = defaultDeserializerProvider
        if (defaultDeserializer != null) {
            builder.registerDefaultPolymorphicDeserializer(baseClass, defaultDeserializer, false)
        }
    }

}