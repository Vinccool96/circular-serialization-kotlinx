package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.PolymorphicCircularSerializer
import org.cirjson.serialization.SerializableWith
import kotlin.reflect.KClass
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KType

internal actual fun KClass<*>.platformSpecificSerializerNotRegistered(): Nothing {
    throw CircularSerializationException(
            "${notRegisteredMessage()}\n" + "On Kotlin/JS explicitly declared serializer should be used for interfaces and enums without @Serializable annotation")
}

@Suppress("UNCHECKED_CAST", "DEPRECATION_ERROR")
@OptIn(ExperimentalAssociatedObjects::class)
internal actual fun <T : Any> KClass<T>.constructSerializerForGivenTypeArgs(
        vararg args: CircularKSerializer<Any?>): CircularKSerializer<T>? = try {
    val assocObject = findAssociatedObject<SerializableWith>()
    when {
        assocObject is CircularKSerializer<*> -> assocObject as CircularKSerializer<T>
        assocObject is CircularSerializerFactory -> assocObject.serializer(*args) as CircularKSerializer<T>
        this.isInterface -> PolymorphicCircularSerializer(this)
        else -> null
    }
} catch (e: dynamic) {
    null
}

internal actual fun isReferenceArray(rootClass: KClass<Any>): Boolean = rootClass == Array::class

internal actual fun <T> Array<T>.getChecked(index: Int): T {
    if (index !in indices) throw IndexOutOfBoundsException("Index $index out of bounds $indices")
    return get(index)
}

internal actual fun BooleanArray.getChecked(index: Int): Boolean {
    if (index !in indices) throw IndexOutOfBoundsException("Index $index out of bounds $indices")
    return get(index)
}

@Suppress("UNCHECKED_CAST")
internal actual fun <T : Any> KClass<T>.compiledSerializerImpl(): CircularKSerializer<T>? =
        this.constructSerializerForGivenTypeArgs() ?: this.js.asDynamic().Companion?.serializer() as? CircularKSerializer<T>

internal actual fun <T> createCache(factory: (KClass<*>) -> CircularKSerializer<T>?): CircularSerializerCache<T> {
    return object: CircularSerializerCache<T> {
        override fun get(key: KClass<Any>): CircularKSerializer<T>? {
            return factory(key)
        }
    }
}

internal actual fun <T> createParametrizedCache(factory: (KClass<Any>, List<KType>) -> CircularKSerializer<T>?): CircularParametrizedSerializerCache<T> {
    return object: CircularParametrizedSerializerCache<T> {
        override fun get(key: KClass<Any>, types: List<KType>): Result<CircularKSerializer<T>?> {
            return kotlin.runCatching { factory(key, types) }
        }
    }
}

internal actual fun <T : Any, E : T?> ArrayList<E>.toNativeArrayImpl(eClass: KClass<T>): Array<E> = toTypedArray()
