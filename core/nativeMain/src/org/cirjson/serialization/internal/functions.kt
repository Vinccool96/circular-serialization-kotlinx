package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.SerializableWith
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.findAssociatedObject

internal actual fun KClass<*>.platformSpecificSerializerNotRegistered(): Nothing {
    throw CircularSerializationException(
            "${notRegisteredMessage()}\n" + "On Kotlin/Native explicitly declared serializer should be used for interfaces and enums without @Serializable annotation")
}

@Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
@OptIn(ExperimentalAssociatedObjects::class)
internal actual fun <T : Any> KClass<T>.constructSerializerForGivenTypeArgs(
        vararg args: CircularKSerializer<Any?>): CircularKSerializer<T>? =
        when (val assocObject = findAssociatedObject<SerializableWith>()) {
            is CircularKSerializer<*> -> assocObject as CircularKSerializer<T>
            is CircularSerializerFactory -> assocObject.serializer(*args) as CircularKSerializer<T>
            else -> null
        }

internal actual fun isReferenceArray(rootClass: KClass<Any>): Boolean = rootClass == Array::class

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun <T> Array<T>.getChecked(index: Int): T {
    return get(index)
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun BooleanArray.getChecked(index: Int): Boolean {
    return get(index)
}

@Suppress("DEPRECATION_ERROR")
internal actual fun <T : Any> KClass<T>.compiledSerializerImpl(): CircularKSerializer<T>? =
        this.constructSerializerForGivenTypeArgs()

internal actual fun <T> createCache(factory: (KClass<*>) -> CircularKSerializer<T>?): CircularSerializerCache<T> {
    return object : CircularSerializerCache<T> {

        override fun get(key: KClass<Any>): CircularKSerializer<T>? {
            return factory(key)
        }

    }
}

internal actual fun <T> createParametrizedCache(
        factory: (KClass<Any>, List<KType>) -> CircularKSerializer<T>?): CircularParametrizedSerializerCache<T> {
    return object : CircularParametrizedSerializerCache<T> {

        override fun get(key: KClass<Any>, types: List<KType>): Result<CircularKSerializer<T>?> {
            return kotlin.runCatching { factory(key, types) }
        }

    }
}

internal actual fun <T : Any, E : T?> ArrayList<E>.toNativeArrayImpl(eClass: KClass<T>): Array<E> {
    val result = arrayOfAnyNulls<E>(size)
    var index = 0
    for (element in this) result[index++] = element
    @Suppress("USELESS_CAST") return result as Array<E>
}

@Suppress("UNCHECKED_CAST")
private fun <T> arrayOfAnyNulls(size: Int): Array<T> = arrayOfNulls<Any>(size) as Array<T>
