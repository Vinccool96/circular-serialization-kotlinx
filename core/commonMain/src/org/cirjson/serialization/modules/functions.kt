package org.cirjson.serialization.modules

import org.cirjson.serialization.CircularDeserializationStrategy
import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationStrategy
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import kotlin.reflect.KClass

/**
 * Returns a combination of two serial modules
 *
 * If serializer for some class presents in both modules, a [CircularSerializerAlreadyRegisteredException] is thrown.
 * To overwrite serializers, use [CircularSerializersModule.overwriteWith] function.
 */
public operator fun CircularSerializersModule.plus(other: CircularSerializersModule): CircularSerializersModule =
        CircularSerializersModule {
            include(this@plus)
            include(other)
        }

/**
 * Returns a combination of two serial modules
 *
 * If serializer for some class presents in both modules, result module
 * will contain serializer from [other] module.
 */
@OptIn(ExperimentalCircularSerializationApi::class)
public infix fun CircularSerializersModule.overwriteWith(other: CircularSerializersModule): CircularSerializersModule =
        CircularSerializersModule {
            include(this@overwriteWith)
            other.dumpTo(object : CircularSerializersModuleCollector {

                override fun <T : Any> contextual(kClass: KClass<T>, serializer: CircularKSerializer<T>) {
                    registerSerializer(kClass, CircularContextualProvider.Argless(serializer), allowOverwrite = true)
                }

                override fun <T : Any> contextual(kClass: KClass<T>,
                        provider: (serializers: List<CircularKSerializer<*>>) -> CircularKSerializer<*>) {
                    registerSerializer(kClass, CircularContextualProvider.WithTypeArguments(provider),
                            allowOverwrite = true)
                }

                override fun <Base : Any, Sub : Base> polymorphic(baseClass: KClass<Base>, actualClass: KClass<Sub>,
                        actualSerializer: CircularKSerializer<Sub>) {
                    registerPolymorphicSerializer(baseClass, actualClass, actualSerializer, allowOverwrite = true)
                }

                override fun <Base : Any> polymorphicDefaultSerializer(baseClass: KClass<Base>,
                        defaultSerializerProvider: (value: Base) -> CircularSerializationStrategy<Base>?) {
                    registerDefaultPolymorphicSerializer(baseClass, defaultSerializerProvider, allowOverwrite = true)
                }

                override fun <Base : Any> polymorphicDefaultDeserializer(baseClass: KClass<Base>,
                        defaultDeserializerProvider: (className: String?) -> CircularDeserializationStrategy<Base>?) {
                    registerDefaultPolymorphicDeserializer(baseClass, defaultDeserializerProvider,
                            allowOverwrite = true)
                }

            })
        }

/**
 * Returns a [CircularSerializersModule] which has one class with one [serializer] for [ContextualSerializer].
 */
public fun <T : Any> serializersModuleOf(kClass: KClass<T>,
        serializer: CircularKSerializer<T>): CircularSerializersModule =
        CircularSerializersModule { contextual(kClass, serializer) }

/**
 * Returns a [CircularSerializersModule] which has one class with one [serializer] for [ContextualSerializer].
 */
public inline fun <reified T : Any> serializersModuleOf(serializer: CircularKSerializer<T>): CircularSerializersModule =
        serializersModuleOf(T::class, serializer)

/**
 * A builder function for creating a [CircularSerializersModule].
 * Serializers can be added via [CircularSerializersModuleBuilder.contextual] or [CircularSerializersModuleBuilder.polymorphic].
 * Since [CircularSerializersModuleBuilder] also implements [CircularSerializersModuleCollector],
 * it is possible to copy whole another module to this builder with [CircularSerializersModule.dumpTo]
 */
@Suppress("FunctionName")
public inline fun CircularSerializersModule(
        builderAction: CircularSerializersModuleBuilder.() -> Unit): CircularSerializersModule {
    val builder = CircularSerializersModuleBuilder()
    builder.builderAction()
    return builder.build()
}

/**
 * A [CircularSerializersModule] which is empty and returns `null` from each method.
 */
@Suppress("FunctionName")
public fun EmptyCircularSerializersModule(): CircularSerializersModule = @Suppress("DEPRECATION") EmptyCircularSerializersModule

/**
 * Adds [serializer] associated with given type [T] for contextual serialization.
 * Throws [CircularSerializationException] if a module already has serializer associated with the given type.
 * To overwrite an already registered serializer, [CircularSerializersModule.overwriteWith] can be used.
 */
public inline fun <reified T : Any> CircularSerializersModuleBuilder.contextual(
        serializer: CircularKSerializer<T>): Unit = contextual(T::class, serializer)

/**
 * Creates a builder to register subclasses of a given [baseClass] for polymorphic serialization.
 * If [baseSerializer] is not null, registers it as a serializer for [baseClass],
 * which is useful if the base class is serializable itself. To register subclasses,
 * [PolymorphicCircularModuleBuilder.subclass] builder function can be used.
 *
 * If a serializer already registered for the given KClass in the given scope, an [IllegalArgumentException] is thrown.
 * To override registered serializers, combine built module with another using [CircularSerializersModule.overwriteWith].
 *
 * @see PolymorphicSerializer
 */
public inline fun <Base : Any> CircularSerializersModuleBuilder.polymorphic(baseClass: KClass<Base>,
        baseSerializer: CircularKSerializer<Base>? = null,
        builderAction: PolymorphicCircularModuleBuilder<Base>.() -> Unit = {}) {
    val builder = PolymorphicCircularModuleBuilder(baseClass, baseSerializer)
    builder.builderAction()
    builder.buildTo(this)
}

/**
 * Registers a [subclass] [serializer] in the resulting module under the [base class][Base].
 */
public inline fun <Base : Any, reified T : Base> PolymorphicCircularModuleBuilder<Base>.subclass(
        serializer: CircularKSerializer<T>): Unit = subclass(T::class, serializer)

/**
 * Registers a serializer for class [T] in the resulting module under the [base class][Base].
 */
public inline fun <Base : Any, reified T : Base> PolymorphicCircularModuleBuilder<Base>.subclass(
        clazz: KClass<T>): Unit = subclass(clazz, serializer())

