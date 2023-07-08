package org.cirjson.serialization.modules

import org.cirjson.serialization.*
import kotlin.reflect.KClass

/**
 * [CircularSerializersModule] is a collection of serializers used by [ContextualSerializer] and [PolymorphicSerializer]
 * to override or provide serializers at the runtime, whereas at the compile-time they provided by the serialization plugin.
 * It can be considered as a map where serializers can be found using their statically known KClasses.
 *
 * To enable runtime serializers resolution, one of the special annotations must be used on target types
 * ([Polymorphic] or [Contextual]), and a serial module with serializers should be used during construction of [SerialFormat].
 *
 * Serializers module can be built with `SerializersModule {}` builder function.
 * Empty module can be obtained with `EmptySerializersModule()` factory function.
 *
 * @see Contextual
 * @see Polymorphic
 */
public sealed class CircularSerializersModule {

    @ExperimentalCircularSerializationApi
    @Deprecated("Deprecated in favor of overload with default parameter", ReplaceWith("getContextual(kclass)"),
            DeprecationLevel.HIDDEN) // Was experimental since 1.0.0, HIDDEN in 1.2.0 in a backwards-compatible manner
    public fun <T : Any> getContextual(kclass: KClass<T>): CircularKSerializer<T>? = getContextual(kclass, emptyList())

    /**
     * Returns a contextual serializer associated with a given [kClass].
     * If given class has generic parameters and module has provider for [kClass],
     * [typeArgumentsSerializers] are used to create serializer.
     * This method is used in context-sensitive operations on a property marked with [Contextual] by a [ContextualSerializer].
     *
     * @see CircularSerializersModuleBuilder.contextual
     */
    @ExperimentalCircularSerializationApi
    public abstract fun <T : Any> getContextual(kClass: KClass<T>,
            typeArgumentsSerializers: List<CircularKSerializer<*>> = emptyList()): CircularKSerializer<T>?

    /**
     * Returns a polymorphic serializer registered for a class of the given [value] in the scope of [baseClass].
     */
    @ExperimentalCircularSerializationApi
    public abstract fun <T : Any> getPolymorphic(baseClass: KClass<in T>, value: T): CircularSerializationStrategy<T>?

    /**
     * Returns a polymorphic deserializer registered for a [serializedClassName] in the scope of [baseClass]
     * or default value constructed from [serializedClassName] if a default serializer provider was registered.
     */
    @ExperimentalCircularSerializationApi
    public abstract fun <T : Any> getPolymorphic(baseClass: KClass<in T>,
            serializedClassName: String?): CircularDeserializationStrategy<T>?

    /**
     * Copies contents of this module to the given [collector].
     */
    @ExperimentalCircularSerializationApi
    public abstract fun dumpTo(collector: CircularSerializersModuleCollector)

}