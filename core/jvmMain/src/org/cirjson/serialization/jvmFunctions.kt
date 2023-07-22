@file:Suppress("UNCHECKED_CAST")

package org.cirjson.serialization

import org.cirjson.serialization.builtins.*
import org.cirjson.serialization.internal.builtinSerializerOrNull
import org.cirjson.serialization.internal.serializerNotRegistered
import org.cirjson.serialization.internal.constructSerializerForGivenTypeArgs
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.modules.EmptyCircularSerializersModule
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Reflectively retrieves a serializer for the given [type].
 *
 * This overload is intended to be used as an interoperability layer for JVM-centric libraries,
 * that operate with Java's type tokens and cannot use Kotlin's [KType] or [typeOf].
 * For application-level serialization, it is recommended to use `serializer<T>()` or `serializer(KType)` instead as it is aware of
 * Kotlin-specific type information, such as nullability, sealed classes and object singletons.
 *
 * Note that because [Type] does not contain any information about nullability, all created serializers
 * work only with non-nullable data.
 *
 * Not all [Type] implementations are supported.
 * [type] must be an instance of [Class], [GenericArrayType], [ParameterizedType] or [WildcardType].
 *
 * @throws CircularSerializationException if serializer cannot be created (provided [type] or its type argument is not serializable).
 * @throws IllegalArgumentException if an unsupported subclass of [Type] is provided.
 */
public fun serializer(type: Type): CircularKSerializer<Any> = EmptyCircularSerializersModule().serializer(type)

/**
 * Reflectively retrieves a serializer for the given [type].
 *
 * This overload is intended to be used as an interoperability layer for JVM-centric libraries,
 * that operate with Java's type tokens and cannot use Kotlin's [KType] or [typeOf].
 * For application-level serialization, it is recommended to use `serializer<T>()` or `serializer(KType)` instead as it is aware of
 * Kotlin-specific type information, such as nullability, sealed classes and object singletons.
 *
 * Note that because [Type] does not contain any information about nullability, all created serializers
 * work only with non-nullable data.
 *
 * Not all [Type] implementations are supported.
 * [type] must be an instance of [Class], [GenericArrayType], [ParameterizedType] or [WildcardType].
 *
 * @return [CircularKSerializer] for given [type] or `null` if serializer cannot be created (given [type] or its type argument is not serializable).
 * @throws IllegalArgumentException if an unsupported subclass of [Type] is provided.
 */
public fun serializerOrNull(type: Type): CircularKSerializer<Any>? =
        EmptyCircularSerializersModule().serializerOrNull(type)

/**
 * Retrieves a serializer for the given [type] using
 * reflective construction and [contextual][CircularSerializersModule.getContextual] lookup as a fallback for non-serializable types.
 *
 * This overload is intended to be used as an interoperability layer for JVM-centric libraries,
 * that operate with Java's type tokens and cannot use Kotlin's [KType] or [typeOf].
 * For application-level serialization, it is recommended to use `serializer<T>()` or `serializer(KType)` instead as it is aware of
 * Kotlin-specific type information, such as nullability, sealed classes and object singletons.
 *
 * Note that because [Type] does not contain any information about nullability, all created serializers
 * work only with non-nullable data.
 *
 * Not all [Type] implementations are supported.
 * [type] must be an instance of [Class], [GenericArrayType], [ParameterizedType] or [WildcardType].
 *
 * @throws CircularSerializationException if serializer cannot be created (provided [type] or its type argument is not serializable).
 * @throws IllegalArgumentException if an unsupported subclass of [Type] is provided.
 */
public fun CircularSerializersModule.serializer(type: Type): CircularKSerializer<Any> =
        serializerByJavaTypeImpl(type, failOnMissingTypeArgSerializer = true) ?: type.prettyClass()
                .serializerNotRegistered()

/**
 * Retrieves a serializer for the given [type] using
 * reflective construction and [contextual][CircularSerializersModule.getContextual] lookup as a fallback for non-serializable types.
 *
 * This overload is intended to be used as an interoperability layer for JVM-centric libraries,
 * that operate with Java's type tokens and cannot use Kotlin's [KType] or [typeOf].
 * For application-level serialization, it is recommended to use `serializer<T>()` or `serializer(KType)` instead as it is aware of
 * Kotlin-specific type information, such as nullability, sealed classes and object singletons.
 *
 * Note that because [Type] does not contain any information about nullability, all created serializers
 * work only with non-nullable data.
 *
 * Not all [Type] implementations are supported.
 * [type] must be an instance of [Class], [GenericArrayType], [ParameterizedType] or [WildcardType].
 *
 * @return [CircularKSerializer] for given [type] or `null` if serializer cannot be created (given [type] or its type argument is not serializable).
 * @throws IllegalArgumentException if an unsupported subclass of [Type] is provided.
 */
public fun CircularSerializersModule.serializerOrNull(type: Type): CircularKSerializer<Any>? =
        serializerByJavaTypeImpl(type, failOnMissingTypeArgSerializer = false)

private fun CircularSerializersModule.serializerByJavaTypeImpl(type: Type,
        failOnMissingTypeArgSerializer: Boolean = true): CircularKSerializer<Any>? = when (type) {
    is GenericArrayType -> {
        genericArraySerializer(type, failOnMissingTypeArgSerializer)
    }
    is Class<*> -> typeSerializer(type, failOnMissingTypeArgSerializer)
    is ParameterizedType -> {
        val rootClass = (type.rawType as Class<*>)
        val args = (type.actualTypeArguments)
        val argsSerializers = if (failOnMissingTypeArgSerializer) args.map { serializer(it) } else args.map {
            serializerOrNull(it) ?: return null
        }
        when {
            Set::class.java.isAssignableFrom(rootClass) -> CircularSetSerializer(
                    argsSerializers[0]) as CircularKSerializer<Any>
            List::class.java.isAssignableFrom(rootClass) || Collection::class.java.isAssignableFrom(
                    rootClass) -> CircularListSerializer(argsSerializers[0]) as CircularKSerializer<Any>
            Map::class.java.isAssignableFrom(rootClass) -> CircularMapSerializer(argsSerializers[0],
                    argsSerializers[1]) as CircularKSerializer<Any>
            Map.Entry::class.java.isAssignableFrom(rootClass) -> CircularMapEntrySerializer(argsSerializers[0],
                    argsSerializers[1]) as CircularKSerializer<Any>
            Pair::class.java.isAssignableFrom(rootClass) -> CircularPairSerializer(argsSerializers[0],
                    argsSerializers[1]) as CircularKSerializer<Any>
            Triple::class.java.isAssignableFrom(rootClass) -> CircularTripleSerializer(argsSerializers[0],
                    argsSerializers[1], argsSerializers[2]) as CircularKSerializer<Any>

            else -> {
                val varargs = argsSerializers.map { it as CircularKSerializer<Any?> }
                reflectiveOrContextual(rootClass as Class<Any>, varargs)
            }
        }
    }
    is WildcardType -> serializerByJavaTypeImpl(type.upperBounds.first())
    else -> throw IllegalArgumentException(
            "type should be an instance of Class<?>, GenericArrayType, ParametrizedType or WildcardType, but actual argument $type has type ${type::class}")
}

@OptIn(ExperimentalCircularSerializationApi::class)
private fun CircularSerializersModule.typeSerializer(type: Class<*>,
        failOnMissingTypeArgSerializer: Boolean): CircularKSerializer<Any>? {
    return if (type.isArray && !type.componentType.isPrimitive) {
        val eType: Class<*> = type.componentType
        val s = if (failOnMissingTypeArgSerializer) serializer(eType) else (serializerOrNull(eType) ?: return null)
        val arraySerializer = CircularArraySerializer(eType.kotlin as KClass<Any>, s)
        arraySerializer as CircularKSerializer<Any>
    } else {
        reflectiveOrContextual(type as Class<Any>, emptyList())
    }
}

@OptIn(ExperimentalCircularSerializationApi::class)
private fun <T : Any> CircularSerializersModule.reflectiveOrContextual(jClass: Class<T>,
        typeArgumentsSerializers: List<CircularKSerializer<Any?>>): CircularKSerializer<T>? {
    jClass.constructSerializerForGivenTypeArgs(*typeArgumentsSerializers.toTypedArray())?.let { return it }
    val kClass = jClass.kotlin
    return kClass.builtinSerializerOrNull() ?: getContextual(kClass, typeArgumentsSerializers)
}

@OptIn(ExperimentalCircularSerializationApi::class)
private fun CircularSerializersModule.genericArraySerializer(type: GenericArrayType,
        failOnMissingTypeArgSerializer: Boolean): CircularKSerializer<Any>? {
    val eType = type.genericComponentType.let {
        when (it) {
            is WildcardType -> it.upperBounds.first()
            else -> it
        }
    }
    val serializer = if (failOnMissingTypeArgSerializer) serializer(eType) else (serializerOrNull(eType) ?: return null)
    val kclass = when (eType) {
        is ParameterizedType -> (eType.rawType as Class<*>).kotlin
        is KClass<*> -> eType
        else -> throw IllegalStateException("unsupported type in GenericArray: ${eType::class}")
    } as KClass<Any>
    return CircularArraySerializer(kclass, serializer) as CircularKSerializer<Any>
}

private fun Type.prettyClass(): Class<*> = when (val it = this) {
    is Class<*> -> it
    is ParameterizedType -> it.rawType.prettyClass()
    is WildcardType -> it.upperBounds.first().prettyClass()
    is GenericArrayType -> it.genericComponentType.prettyClass()
    else -> throw IllegalArgumentException(
            "type should be an instance of Class<?>, GenericArrayType, ParametrizedType or WildcardType, but actual argument $it has type ${it::class}")
}
