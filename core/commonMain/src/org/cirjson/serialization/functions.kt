@file:Suppress("UNCHECKED_CAST")

package org.cirjson.serialization

import org.cirjson.serialization.builtins.CircularArraySerializer
import org.cirjson.serialization.builtins.nullable
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import org.cirjson.serialization.internal.*
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.modules.EmptyCircularSerializersModule
import kotlin.reflect.*

/**
 * Serializes and encodes the given [value] to string using serializer retrieved from the reified type parameter.
 *
 * @throws CircularSerializationException in case of any encoding-specific error
 * @throws IllegalArgumentException if the encoded input does not comply format's specification
 */
public inline fun <reified T> StringFormat.encodeToString(value: T): String =
        encodeToString(serializersModule.serializer(), value)

/**
 * Decodes and deserializes the given [string] to the value of type [T] using deserializer
 * retrieved from the reified type parameter.
 *
 * @throws CircularSerializationException in case of any decoding-specific error
 * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
 */
public inline fun <reified T> StringFormat.decodeFromString(string: String): T =
        decodeFromString(serializersModule.serializer(), string)

/**
 * Serializes and encodes the given [value] to byte array, delegating it to the [BinaryFormat],
 * and then encodes resulting bytes to hex string.
 *
 * Hex representation does not interfere with serialization and encoding process of the format and
 * only applies transformation to the resulting array. It is recommended to use for debugging and
 * testing purposes.
 *
 * @throws CircularSerializationException in case of any encoding-specific error
 * @throws IllegalArgumentException if the encoded input does not comply format's specification
 */
public fun <T> BinaryFormat.encodeToHexString(serializer: CircularSerializationStrategy<T>, value: T): String =
        InternalHexConverter.printHexBinary(encodeToByteArray(serializer, value), lowerCase = true)

/**
 * Decodes byte array from the given [hex] string and the decodes and deserializes it
 * to the value of type [T], delegating it to the [BinaryFormat].
 *
 * This method is a counterpart to [encodeToHexString].
 *
 * @throws CircularSerializationException in case of any decoding-specific error
 * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
 */
public fun <T> BinaryFormat.decodeFromHexString(deserializer: CircularDeserializationStrategy<T>, hex: String): T =
        decodeFromByteArray(deserializer, InternalHexConverter.parseHexBinary(hex))

/**
 * Serializes and encodes the given [value] to byte array, delegating it to the [BinaryFormat],
 * and then encodes resulting bytes to hex string.
 *
 * Hex representation does not interfere with serialization and encoding process of the format and
 * only applies transformation to the resulting array. It is recommended to use for debugging and
 * testing purposes.
 *
 * @throws CircularSerializationException in case of any encoding-specific error
 * @throws IllegalArgumentException if the encoded input does not comply format's specification
 */
public inline fun <reified T> BinaryFormat.encodeToHexString(value: T): String =
        encodeToHexString(serializersModule.serializer(), value)

/**
 * Decodes byte array from the given [hex] string and the decodes and deserializes it
 * to the value of type [T], delegating it to the [BinaryFormat].
 *
 * This method is a counterpart to [encodeToHexString].
 *
 * @throws CircularSerializationException in case of any decoding-specific error
 * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
 */
public inline fun <reified T> BinaryFormat.decodeFromHexString(hex: String): T =
        decodeFromHexString(serializersModule.serializer(), hex)

/**
 * Serializes and encodes the given [value] to byte array using serializer
 * retrieved from the reified type parameter.
 *
 * @throws CircularSerializationException in case of any encoding-specific error
 * @throws IllegalArgumentException if the encoded input does not comply format's specification
 */
public inline fun <reified T> BinaryFormat.encodeToByteArray(value: T): ByteArray =
        encodeToByteArray(serializersModule.serializer(), value)

/**
 * Decodes and deserializes the given [byte array][bytes] to the value of type [T] using deserializer
 * retrieved from the reified type parameter.
 *
 * @throws CircularSerializationException in case of any decoding-specific error
 * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
 */
public inline fun <reified T> BinaryFormat.decodeFromByteArray(bytes: ByteArray): T =
        decodeFromByteArray(serializersModule.serializer(), bytes)

@InternalCircularSerializationApi
public fun <T : Any> AbstractPolymorphicCircularSerializer<T>.findPolymorphicSerializer(
        decoder: CircularCompositeDecoder, klassName: String?): CircularDeserializationStrategy<T> =
        findPolymorphicSerializerOrNull(decoder, klassName) ?: throwSubtypeNotRegistered(klassName, baseClass)

@InternalCircularSerializationApi
public fun <T : Any> AbstractPolymorphicCircularSerializer<T>.findPolymorphicSerializer(encoder: CircularEncoder,
        value: T): CircularSerializationStrategy<T> =
        findPolymorphicSerializerOrNull(encoder, value) ?: throwSubtypeNotRegistered(value::class, baseClass)

/**
 * Retrieves a serializer for the given type [T].
 * This overload is a reified version of `serializer(KType)`.
 *
 * This overload works with full type information, including type arguments and nullability,
 * and is a recommended way to retrieve a serializer.
 * For example, `serializer<List<String?>>()` returns [CircularKSerializer] that is able
 * to serialize and deserialize list of nullable strings — i.e. `ListSerializer(String.serializer().nullable)`.
 *
 * Variance of [T]'s type arguments is not used by the serialization and is not taken into account.
 * Star projections in [T]'s type arguments are prohibited.
 *
 * @throws CircularSerializationException if serializer cannot be created (provided [T] or its type argument is not serializable).
 * @throws IllegalArgumentException if any of [T]'s type arguments contains star projection
 */
public inline fun <reified T> serializer(): CircularKSerializer<T> {
    return serializer(typeOf<T>()).cast()
}

/**
 * Retrieves default serializer for the given type [T] and,
 * if [T] is not serializable, fallbacks to [contextual][CircularSerializersModule.getContextual] lookup.
 *
 * This overload works with full type information, including type arguments and nullability,
 * and is a recommended way to retrieve a serializer.
 * For example, `serializer<List<String?>>()` returns [CircularKSerializer] that is able
 * to serialize and deserialize list of nullable strings — i.e. `ListSerializer(String.serializer().nullable)`.
 *
 * Variance of [T]'s type arguments is not used by the serialization and is not taken into account.
 * Star projections in [T]'s type arguments are prohibited.
 *
 * @throws CircularSerializationException if serializer cannot be created (provided [T] or its type argument is not serializable).
 * @throws IllegalArgumentException if any of [T]'s type arguments contains star projection
 */
public inline fun <reified T> CircularSerializersModule.serializer(): CircularKSerializer<T> {
    return serializer(typeOf<T>()).cast()
}

/**
 * Creates a serializer for the given [type].
 * [type] argument is usually obtained with [typeOf] method.
 *
 * This overload works with full type information, including type arguments and nullability,
 * and is a recommended way to retrieve a serializer.
 * For example, `serializer<typeOf<List<String?>>>()` returns [CircularKSerializer] that is able
 * to serialize and deserialize list of nullable strings — i.e. `ListSerializer(String.serializer().nullable)`.
 *
 * Variance of [type]'s type arguments is not used by the serialization and is not taken into account.
 * Star projections in [type]'s arguments are prohibited.
 *
 * @throws CircularSerializationException if serializer cannot be created (provided [type] or its type argument is not serializable).
 * @throws IllegalArgumentException if any of [type]'s arguments contains star projection
 */
public fun serializer(type: KType): CircularKSerializer<Any?> = EmptyCircularSerializersModule().serializer(type)

/**
 * Retrieves serializer for the given [kClass].
 * This method uses platform-specific reflection available.
 *
 * If [kClass] is a parametrized type then it is necessary to pass serializers for generic parameters in the [typeArgumentsSerializers].
 * The nullability of returned serializer is specified using the [isNullable].
 *
 * Note that it is impossible to create an array serializer with this method,
 * as array serializer needs additional information: type token for an element type.
 * To create array serializer, use overload with [KType] or [CircularArraySerializer] directly.
 *
 * Caching on JVM platform is disabled for this function, so it may work slower than an overload with [KType].
 *
 * @throws CircularSerializationException if serializer cannot be created (provided [kClass] or its type argument is not serializable)
 * @throws CircularSerializationException if [kClass] is a `kotlin.Array`
 * @throws CircularSerializationException if size of [typeArgumentsSerializers] does not match the expected generic parameters count
 */
@ExperimentalCircularSerializationApi
public fun serializer(kClass: KClass<*>, typeArgumentsSerializers: List<CircularKSerializer<*>>,
        isNullable: Boolean): CircularKSerializer<Any?> =
        EmptyCircularSerializersModule().serializer(kClass, typeArgumentsSerializers, isNullable)

/**
 * Creates a serializer for the given [type] if possible.
 * [type] argument is usually obtained with [typeOf] method.
 *
 * This overload works with full type information, including type arguments and nullability,
 * and is a recommended way to retrieve a serializer.
 * For example, `serializerOrNull<typeOf<List<String?>>>()` returns [CircularKSerializer] that is able
 * to serialize and deserialize list of nullable strings — i.e. `ListSerializer(String.serializer().nullable)`.
 *
 * Variance of [type]'s arguments is not used by the serialization and is not taken into account.
 * Star projections in [type]'s arguments are prohibited.
 *
 * @return [CircularKSerializer] for the given [type] or `null` if serializer cannot be created (given [type] or its type argument is not serializable).
 * @throws IllegalArgumentException if any of [type]'s arguments contains star projection
 */
public fun serializerOrNull(type: KType): CircularKSerializer<Any?>? =
        EmptyCircularSerializersModule().serializerOrNull(type)

/**
 * Retrieves default serializer for the given [type] and,
 * if [type] is not serializable, fallbacks to [contextual][CircularSerializersModule.getContextual] lookup.
 * [type] argument is usually obtained with [typeOf] method.
 *
 * This overload works with full type information, including type arguments and nullability,
 * and is a recommended way to retrieve a serializer.
 * For example, `serializer<typeOf<List<String?>>>()` returns [CircularKSerializer] that is able
 * to serialize and deserialize list of nullable strings — i.e. `ListSerializer(String.serializer().nullable)`.
 *
 * Variance of [type]'s arguments is not used by the serialization and is not taken into account.
 * Star projections in [type]'s arguments are prohibited.
 *
 * @throws CircularSerializationException if serializer cannot be created (provided [type] or its type argument is not serializable and is not registered in [this] module).
 * @throws IllegalArgumentException if any of [type]'s arguments contains star projection
 */
public fun CircularSerializersModule.serializer(type: KType): CircularKSerializer<Any?> =
        serializerByKTypeImpl(type, failOnMissingTypeArgSerializer = true) ?: type.kclass()
                .platformSpecificSerializerNotRegistered()

/**
 * Retrieves serializer for the given [kClass] and,
 * if [kClass] is not serializable, fallbacks to [contextual][CircularSerializersModule.getContextual] lookup.
 * This method uses platform-specific reflection available.
 *
 * If [kClass] is a parametrized type then it is necessary to pass serializers for generic parameters in the [typeArgumentsSerializers].
 * The nullability of returned serializer is specified using the [isNullable].
 *
 * Note that it is impossible to create an array serializer with this method,
 * as array serializer needs additional information: type token for an element type.
 * To create array serializer, use overload with [KType] or [CircularArraySerializer] directly.
 *
 * Caching on JVM platform is disabled for this function, so it may work slower than an overload with [KType].
 *
 * @throws CircularSerializationException if serializer cannot be created (provided [kClass] or its type argument is not serializable and is not registered in [this] module)
 * @throws CircularSerializationException if [kClass] is a `kotlin.Array`
 * @throws CircularSerializationException if size of [typeArgumentsSerializers] does not match the expected generic parameters count
 */
@ExperimentalCircularSerializationApi
public fun CircularSerializersModule.serializer(kClass: KClass<*>,
        typeArgumentsSerializers: List<CircularKSerializer<*>>, isNullable: Boolean): CircularKSerializer<Any?> =
        serializerByKClassImpl(kClass as KClass<Any>, typeArgumentsSerializers as List<CircularKSerializer<Any?>>,
                isNullable) ?: kClass.platformSpecificSerializerNotRegistered()

/**
 * Retrieves default serializer for the given [type] and,
 * if [type] is not serializable, fallbacks to [contextual][CircularSerializersModule.getContextual] lookup.
 * [type] argument is usually obtained with [typeOf] method.
 *
 * This overload works with full type information, including type arguments and nullability,
 * and is a recommended way to retrieve a serializer.
 * For example, `serializerOrNull<typeOf<List<String?>>>()` returns [CircularKSerializer] that is able
 * to serialize and deserialize list of nullable strings — i.e. `ListSerializer(String.serializer().nullable)`.
 *
 * Variance of [type]'s arguments is not used by the serialization and is not taken into account.
 * Star projections in [type]'s arguments are prohibited.
 *
 * @return [CircularKSerializer] for the given [type] or `null` if serializer cannot be created (given [type] or its type argument is not serializable and is not registered in [this] module).
 * @throws IllegalArgumentException if any of [type]'s arguments contains star projection
 */
public fun CircularSerializersModule.serializerOrNull(type: KType): CircularKSerializer<Any?>? =
        serializerByKTypeImpl(type, failOnMissingTypeArgSerializer = false)

@OptIn(ExperimentalCircularSerializationApi::class)
private fun CircularSerializersModule.serializerByKTypeImpl(type: KType,
        failOnMissingTypeArgSerializer: Boolean): CircularKSerializer<Any?>? {
    val rootClass = type.kclass()
    val isNullable = type.isMarkedNullable
    val typeArguments = type.arguments.map {
        requireNotNull(it.type) { "Star projections in type arguments are not allowed, but had $type" }
    }

    val cachedSerializer = if (typeArguments.isEmpty()) {
        findCachedSerializer(rootClass, isNullable)
    } else {
        val cachedResult = findParametrizedCachedSerializer(rootClass, typeArguments, isNullable)
        if (failOnMissingTypeArgSerializer) {
            cachedResult.getOrNull()
        } else { // return null if error occurred - serializer for parameter(s) was not found
            cachedResult.getOrElse { return null }
        }
    }
    cachedSerializer?.let { return it }

    // slow path to find contextual serializers in serializers module
    val contextualSerializer: CircularKSerializer<out Any?>? = if (typeArguments.isEmpty()) {
        getContextual(rootClass)
    } else {
        val serializers = serializersForParameters(typeArguments, failOnMissingTypeArgSerializer)
                ?: return null // first, we look among the built-in serializers, because the parameter could be contextual
        rootClass.parametrizedSerializerOrNull(serializers) { typeArguments[0].classifier } ?: getContextual(rootClass,
                serializers)
    }
    return contextualSerializer?.cast<Any>()?.nullable(isNullable)
}

@OptIn(ExperimentalCircularSerializationApi::class, InternalCircularSerializationApi::class)
private fun CircularSerializersModule.serializerByKClassImpl(rootClass: KClass<Any>,
        typeArgumentsSerializers: List<CircularKSerializer<Any?>>, isNullable: Boolean): CircularKSerializer<Any?>? {
    val serializer = if (typeArgumentsSerializers.isEmpty()) {
        rootClass.serializerOrNull() ?: getContextual(rootClass)
    } else {
        try {
            rootClass.parametrizedSerializerOrNull(typeArgumentsSerializers) {
                throw CircularSerializationException(
                        "It is not possible to retrieve an array serializer using KClass alone, use KType instead or ArraySerializer factory")
            } ?: getContextual(rootClass, typeArgumentsSerializers)
        } catch (e: IndexOutOfBoundsException) {
            throw CircularSerializationException(
                    "Unable to retrieve a serializer, the number of passed type serializers differs from the actual number of generic parameters",
                    e)
        }
    }

    return serializer?.cast<Any>()?.nullable(isNullable)
}

/**
 * Returns null only if `failOnMissingTypeArgSerializer == false` and at least one parameter serializer not found.
 */
internal fun CircularSerializersModule.serializersForParameters(typeArguments: List<KType>,
        failOnMissingTypeArgSerializer: Boolean): List<CircularKSerializer<Any?>>? {
    val serializers = if (failOnMissingTypeArgSerializer) {
        typeArguments.map { serializer(it) }
    } else {
        typeArguments.map { serializerOrNull(it) ?: return null }
    }
    return serializers
}

/**
 * Retrieves a [CircularKSerializer] for the given [KClass].
 * The given class must be annotated with [Serializable] or be one of the built-in types.
 *
 * This method uses platform-specific reflection available for the given erased `KClass`
 * and is not recommended to use this method for anything, but last-ditch resort, e.g.
 * when all type info is lost, your application has crashed and it is the final attempt to log or send some serializable data.
 *
 * The recommended way to retrieve the serializer is inline [serializer] function and [`serializer(KType)`][serializer]
 *
 * This API is not guaranteed to work consistently across different platforms or
 * to work in cases that slightly differ from "plain @Serializable class" and have platform and reflection specific limitations.
 *
 * ### Constraints
 * This paragraph explains known (but not all!) constraints of the `serializer()` implementation.
 * Please note that they are not bugs, but implementation restrictions that we cannot workaround.
 *
 * * This method may behave differently on JVM, JS and Native because of runtime reflection differences
 * * Serializers for classes with generic parameters are ignored by this method
 * * External serializers generated with `Serializer(forClass = )` are not lookuped consistently
 * * Serializers for classes with named companion objects  are not lookuped consistently
 *
 * @throws CircularSerializationException if serializer can't be found.
 */
@InternalCircularSerializationApi
public fun <T : Any> KClass<T>.serializer(): CircularKSerializer<T> = serializerOrNull() ?: serializerNotRegistered()

/**
 * Retrieves a [CircularKSerializer] for the given [KClass] or returns `null` if none is found.
 * The given class must be annotated with [Serializable] or be one of the built-in types.
 * This method uses platform-specific reflection available for the given erased `KClass`
 * and it is not recommended to use this method for anything, but last-ditch resort, e.g.
 * when all type info is lost, your application has crashed and it is the final attempt to log or send some serializable data.
 *
 * This API is not guaranteed to work consistently across different platforms or
 * to work in cases that slightly differ from "plain @Serializable class".
 *
 * ### Constraints
 * This paragraph explains known (but not all!) constraints of the `serializerOrNull()` implementation.
 * Please note that they are not bugs, but implementation restrictions that we cannot workaround.
 *
 * * This method may behave differently on JVM, JS and Native because of runtime reflection differences
 * * Serializers for classes with generic parameters are ignored by this method
 * * External serializers generated with `Serializer(forClass = )` are not lookuped consistently
 * * Serializers for classes with named companion objects  are not lookuped consistently
 */
@InternalCircularSerializationApi
public fun <T : Any> KClass<T>.serializerOrNull(): CircularKSerializer<T>? =
        compiledSerializerImpl() ?: builtinSerializerOrNull()

internal fun KClass<Any>.parametrizedSerializerOrNull(serializers: List<CircularKSerializer<Any?>>,
        elementClassifierIfArray: () -> KClassifier?): CircularKSerializer<out Any>? { // builtin first because some standard parametrized interfaces (e.g. Map) must use builtin serializer but not polymorphic
    return builtinParametrizedSerializer(serializers, elementClassifierIfArray) ?: compiledParametrizedSerializer(
            serializers)
}

private fun KClass<Any>.compiledParametrizedSerializer(
        serializers: List<CircularKSerializer<Any?>>): CircularKSerializer<out Any>? {
    return constructSerializerForGivenTypeArgs(*serializers.toTypedArray())
}

@OptIn(ExperimentalCircularSerializationApi::class, InternalCircularSerializationApi::class)
private fun KClass<Any>.builtinParametrizedSerializer(serializers: List<CircularKSerializer<Any?>>,
        elementClassifierIfArray: () -> KClassifier?): CircularKSerializer<out Any>? {
    return when (this) {
        Collection::class, List::class, MutableList::class, ArrayList::class -> CircularArrayListSerializer(
                serializers[0])
        HashSet::class -> CircularHashSetSerializer(serializers[0])
        Set::class, MutableSet::class, LinkedHashSet::class -> CircularLinkedHashSetSerializer(serializers[0])
        HashMap::class -> CircularHashMapSerializer(serializers[0], serializers[1])
        Map::class, MutableMap::class, LinkedHashMap::class -> CircularLinkedHashMapSerializer(serializers[0],
                serializers[1])

        Map.Entry::class -> CircularMapEntrySerializer(serializers[0], serializers[1])
        Pair::class -> CircularPairSerializer(serializers[0], serializers[1])
        Triple::class -> CircularTripleSerializer(serializers[0], serializers[1], serializers[2])
        else -> {
            if (isReferenceArray(this)) {
                CircularArraySerializer(elementClassifierIfArray() as KClass<Any>, serializers[0])
            } else {
                null
            }
        }
    }
}

private fun <T : Any> CircularKSerializer<T>.nullable(shouldBeNullable: Boolean): CircularKSerializer<T?> {
    if (shouldBeNullable) return nullable
    return this as CircularKSerializer<T?>
}

/**
 * Overloads of [noCompiledSerializer] should never be called directly.
 * Instead, compiler inserts calls to them when intrinsifying [serializer] function.
 *
 * If no serializer has been found in compile time, call to [noCompiledSerializer] inserted instead.
 */
@Suppress("unused")
@PublishedApi
internal fun noCompiledSerializer(forClass: String): CircularKSerializer<*> =
        throw CircularSerializationException(notRegisteredMessage(forClass))

// Used when compiler intrinsic is inserted
@OptIn(ExperimentalCircularSerializationApi::class)
@Suppress("unused")
@PublishedApi
internal fun noCompiledSerializer(module: CircularSerializersModule, kClass: KClass<*>): CircularKSerializer<*> {
    return module.getContextual(kClass) ?: kClass.serializerNotRegistered()
}

@OptIn(ExperimentalCircularSerializationApi::class)
@Suppress("unused")
@PublishedApi
internal fun noCompiledSerializer(module: CircularSerializersModule, kClass: KClass<*>,
        argSerializers: Array<CircularKSerializer<*>>): CircularKSerializer<*> {
    return module.getContextual(kClass, argSerializers.asList()) ?: kClass.serializerNotRegistered()
}

