package org.cirjson.serialization.modules

import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationStrategy
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

/**
 * A builder class for [CircularSerializersModule] DSL. To create an instance of builder, use [CircularSerializersModule] factory function.
 */
@OptIn(ExperimentalCircularSerializationApi::class)
public class CircularSerializersModuleBuilder @PublishedApi internal constructor() :
        CircularSerializersModuleCollector {

    private val class2ContextualProvider: MutableMap<KClass<*>, CircularContextualProvider> = hashMapOf()
    private val polyBase2Serializers: MutableMap<KClass<*>, MutableMap<KClass<*>, CircularKSerializer<*>>> = hashMapOf()
    private val polyBase2DefaultSerializerProvider: MutableMap<KClass<*>, PolymorphicCircularSerializerProvider<*>> =
            hashMapOf()
    private val polyBase2NamedSerializers: MutableMap<KClass<*>, MutableMap<String, CircularKSerializer<*>>> =
            hashMapOf()
    private val polyBase2DefaultDeserializerProvider: MutableMap<KClass<*>, PolymorphicCircularDeserializerProvider<*>> =
            hashMapOf()

    /**
     * Adds [serializer] associated with given [kClass] for contextual serialization.
     * If [kClass] has generic type parameters, consider registering provider instead.
     *
     * Throws [CircularSerializationException] if a module already has serializer or provider associated with a [kClass].
     * To overwrite an already registered serializer, [CircularSerializersModule.overwriteWith] can be used.
     */
    public override fun <T : Any> contextual(kClass: KClass<T>, serializer: CircularKSerializer<T>): Unit =
            registerSerializer(kClass, CircularContextualProvider.Argless(serializer))

    /**
     * Registers [provider] associated with given generic [kClass] for contextual serialization.
     * When a serializer is requested from a module, provider is being called with type arguments serializers
     * of the particular [kClass] usage.
     *
     * Example:
     * ```
     * class Holder(@Contextual val boxI: Box<Int>, @Contextual val boxS: Box<String>)
     *
     * val module = SerializersModule {
     *   // args[0] contains Int.serializer() or String.serializer(), depending on the property
     *   contextual(Box::class) { args -> BoxSerializer(args[0]) }
     * }
     * ```
     *
     * Throws [CircularSerializationException] if a module already has provider or serializer associated with a [kClass].
     * To overwrite an already registered serializer, [CircularSerializersModule.overwriteWith] can be used.
     */
    public override fun <T : Any> contextual(kClass: KClass<T>,
            provider: (typeArgumentsSerializers: List<CircularKSerializer<*>>) -> CircularKSerializer<*>): Unit =
            registerSerializer(kClass, CircularContextualProvider.WithTypeArguments(provider))

    /**
     * Adds [serializer][actualSerializer] associated with given [actualClass] in the scope of [baseClass] for polymorphic serialization.
     * Throws [CircularSerializationException] if a module already has serializer associated with a [actualClass].
     * To overwrite an already registered serializer, [CircularSerializersModule.overwriteWith] can be used.
     */
    public override fun <Base : Any, Sub : Base> polymorphic(baseClass: KClass<Base>, actualClass: KClass<Sub>,
            actualSerializer: CircularKSerializer<Sub>) {
        registerPolymorphicSerializer(baseClass, actualClass, actualSerializer)
    }

    /**
     * Adds a default serializers provider associated with the given [baseClass] to the resulting module.
     * [defaultSerializerProvider] is invoked when no polymorphic serializers for `value` in the scope of [baseClass] were found.
     *
     * Default serializers provider affects only serialization process. To affect deserialization process, use
     * [CircularSerializersModuleBuilder.polymorphicDefaultDeserializer].
     *
     * [defaultSerializerProvider] can be stateful and lookup a serializer for the missing type dynamically.
     */
    public override fun <Base : Any> polymorphicDefaultSerializer(baseClass: KClass<Base>,
            defaultSerializerProvider: (value: Base) -> CircularSerializationStrategy<Base>?) {
        registerDefaultPolymorphicSerializer(baseClass, defaultSerializerProvider, false)
    }

    /**
     * Adds a default deserializers provider associated with the given [baseClass] to the resulting module.
     * [defaultDeserializerProvider] is invoked when no polymorphic serializers associated with the `className`
     * in the scope of [baseClass] were found. `className` could be `null` for formats that support nullable class discriminators
     * (currently only `Json` with `useArrayPolymorphism` set to `false`).
     *
     * Default deserializers provider affects only deserialization process. To affect serialization process, use
     * [CircularSerializersModuleBuilder.polymorphicDefaultSerializer].
     *
     * [defaultDeserializerProvider] can be stateful and lookup a serializer for the missing type dynamically.
     *
     * @see PolymorphicModuleBuilder.defaultDeserializer
     */
    public override fun <Base : Any> polymorphicDefaultDeserializer(baseClass: KClass<Base>,
            defaultDeserializerProvider: (className: String?) -> CircularDeserializationStrategy<Base>?) {
        registerDefaultPolymorphicDeserializer(baseClass, defaultDeserializerProvider, false)
    }

    /**
     * Copies the content of [module] module into the current builder.
     */
    public fun include(module: CircularSerializersModule) {
        module.dumpTo(this)
    }

    @JvmName("registerSerializer") // Don't mangle method name for prettier stack traces
    internal fun <T : Any> registerSerializer(forClass: KClass<T>, provider: CircularContextualProvider,
            allowOverwrite: Boolean = false) {
        if (!allowOverwrite) {
            val previous = class2ContextualProvider[forClass]
            if (previous != null && previous != provider) { // How can we provide meaningful name for WithTypeArgumentsProvider ?
                throw CircularSerializerAlreadyRegisteredException(
                        "Contextual serializer or serializer provider for $forClass already registered in this module")
            }
        }
        class2ContextualProvider[forClass] = provider
    }

    @JvmName("registerDefaultPolymorphicSerializer") // Don't mangle method name for prettier stack traces
    internal fun <Base : Any> registerDefaultPolymorphicSerializer(baseClass: KClass<Base>,
            defaultSerializerProvider: (value: Base) -> CircularSerializationStrategy<Base>?, allowOverwrite: Boolean) {
        val previous = polyBase2DefaultSerializerProvider[baseClass]
        if (previous != null && previous != defaultSerializerProvider && !allowOverwrite) {
            throw IllegalArgumentException(
                    "Default serializers provider for $baseClass is already registered: $previous")
        }
        polyBase2DefaultSerializerProvider[baseClass] = defaultSerializerProvider
    }

    @JvmName("registerDefaultPolymorphicDeserializer") // Don't mangle method name for prettier stack traces
    internal fun <Base : Any> registerDefaultPolymorphicDeserializer(baseClass: KClass<Base>,
            defaultDeserializerProvider: (className: String?) -> CircularDeserializationStrategy<Base>?,
            allowOverwrite: Boolean) {
        val previous = polyBase2DefaultDeserializerProvider[baseClass]
        if (previous != null && previous != defaultDeserializerProvider && !allowOverwrite) {
            throw IllegalArgumentException(
                    "Default deserializers provider for $baseClass is already registered: $previous")
        }
        polyBase2DefaultDeserializerProvider[baseClass] = defaultDeserializerProvider
    }

    @JvmName("registerPolymorphicSerializer") // Don't mangle method name for prettier stack traces
    internal fun <Base : Any, Sub : Base> registerPolymorphicSerializer(baseClass: KClass<Base>,
            concreteClass: KClass<Sub>, concreteSerializer: CircularKSerializer<Sub>,
            allowOverwrite: Boolean = false) { // Check for overwrite
        val name = concreteSerializer.descriptor.serialName
        val baseClassSerializers = polyBase2Serializers.getOrPut(baseClass, ::hashMapOf)
        val previousSerializer = baseClassSerializers[concreteClass]
        val names = polyBase2NamedSerializers.getOrPut(baseClass, ::hashMapOf)
        if (allowOverwrite) { // Remove previous serializers from name mapping
            if (previousSerializer != null) {
                names.remove(previousSerializer.descriptor.serialName)
            } // Update mappings
            baseClassSerializers[concreteClass] = concreteSerializer
            names[name] = concreteSerializer
            return
        } // Overwrite prohibited
        if (previousSerializer != null) {
            if (previousSerializer != concreteSerializer) {
                throw CircularSerializerAlreadyRegisteredException(baseClass, concreteClass)
            } else { // Cleanup name mapping
                names.remove(previousSerializer.descriptor.serialName)
            }
        }
        val previousByName = names[name]
        if (previousByName != null) {
            val conflictingClass = polyBase2Serializers[baseClass]!!.asSequence().find { it.value === previousByName }
            throw IllegalArgumentException(
                    "Multiple polymorphic serializers for base class '$baseClass' have the same serial name '$name': '$concreteClass' and '$conflictingClass'")
        } // Overwrite if no conflicts
        baseClassSerializers[concreteClass] = concreteSerializer
        names[name] = concreteSerializer
    }

    @PublishedApi
    internal fun build(): CircularSerializersModule =
            CircularSerialModuleImpl(class2ContextualProvider, polyBase2Serializers, polyBase2DefaultSerializerProvider,
                    polyBase2NamedSerializers, polyBase2DefaultDeserializerProvider)
}
