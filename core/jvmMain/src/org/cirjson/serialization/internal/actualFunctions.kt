package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializable
import org.cirjson.serialization.Polymorphic
import org.cirjson.serialization.PolymorphicCircularSerializer
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal actual fun KClass<*>.platformSpecificSerializerNotRegistered(): Nothing = serializerNotRegistered()

internal actual fun <T : Any> KClass<T>.constructSerializerForGivenTypeArgs(
        vararg args: CircularKSerializer<Any?>): CircularKSerializer<T>? {
    return java.constructSerializerForGivenTypeArgs(*args)
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> Class<T>.constructSerializerForGivenTypeArgs(
        vararg args: CircularKSerializer<Any?>): CircularKSerializer<T>? {
    if (isEnum && isNotAnnotated()) {
        return createEnumSerializer()
    }

    // Fall-through if the serializer is not found -- lookup on companions (for sealed interfaces) or fallback to polymorphic if applicable
    if (isInterface) interfaceSerializer()?.let { return it }
    // Search for serializer defined on companion object.
    val serializer = invokeSerializerOnCompanion<T>(this, *args)
    if (serializer != null) return serializer
    // Check whether it's serializable object
    findObjectSerializer()?.let { return it }
    // Search for default serializer if no serializer is defined in companion object.
    // It is required for named companions
    val fromNamedCompanion = try {
        declaredClasses.singleOrNull { it.simpleName == ("\$serializer") }?.getField("INSTANCE")
                ?.get(null) as? CircularKSerializer<T>
    } catch (e: NoSuchFieldException) {
        null
    }
    if (fromNamedCompanion != null) return fromNamedCompanion
    // Check for polymorphic
    return if (isPolymorphicSerializer()) {
        PolymorphicCircularSerializer(this.kotlin)
    } else {
        null
    }
}

internal actual fun isReferenceArray(rootClass: KClass<Any>): Boolean = rootClass.java.isArray

private fun <T : Any> Class<T>.isNotAnnotated(): Boolean {/*
     * For annotated enums search serializer directly (or do not search at all?)
     */
    return getAnnotation(CircularSerializable::class.java) == null && getAnnotation(Polymorphic::class.java) == null
}

private fun <T : Any> Class<T>.isPolymorphicSerializer(): Boolean {/*
     * Last resort: check for @Polymorphic or Serializable(with = PolymorphicSerializer::class)
     * annotations.
     */
    if (getAnnotation(Polymorphic::class.java) != null) {
        return true
    }
    val serializable = getAnnotation(CircularSerializable::class.java)
    return serializable != null && serializable.with == PolymorphicCircularSerializer::class
}

private fun <T : Any> Class<T>.interfaceSerializer(): CircularKSerializer<T>? {/*
     * Interfaces are @Polymorphic by default.
     * Check if it has no annotations or `@Serializable(with = PolymorphicSerializer::class)`,
     * otherwise bailout.
     */
    val serializable = getAnnotation(CircularSerializable::class.java)
    if (serializable == null || serializable.with == PolymorphicCircularSerializer::class) {
        return PolymorphicCircularSerializer(this.kotlin)
    }
    return null
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> invokeSerializerOnCompanion(jClass: Class<*>,
        vararg args: CircularKSerializer<Any?>): CircularKSerializer<T>? {
    val companion = jClass.companionOrNull() ?: return null
    return try {
        val types = if (args.isEmpty()) emptyArray() else Array(args.size) { CircularKSerializer::class.java }
        companion.javaClass.getDeclaredMethod("serializer", *types).invoke(companion, *args) as? CircularKSerializer<T>
    } catch (e: NoSuchMethodException) {
        null
    } catch (e: InvocationTargetException) {
        val cause = e.cause ?: throw e
        throw InvocationTargetException(cause, cause.message ?: e.message)
    }
}

private fun Class<*>.companionOrNull() = try {
    val companion = getDeclaredField("Companion")
    companion.isAccessible = true
    companion.get(null)
} catch (e: Throwable) {
    null
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> Class<T>.createEnumSerializer(): CircularKSerializer<T> {
    val constants = enumConstants
    return CircularEnumSerializer(canonicalName, constants as Array<out Enum<*>>) as CircularKSerializer<T>
}

private fun <T : Any> Class<T>.findObjectSerializer(): CircularKSerializer<T>? {
    // Check it is an object without using kotlin-reflect
    val field =
            declaredFields.singleOrNull { it.name == "INSTANCE" && it.type == this && Modifier.isStatic(it.modifiers) }
                    ?: return null
    // Retrieve its instance and call serializer()
    val instance = field.get(null)
    val method =
            methods.singleOrNull { it.name == "serializer" && it.parameterTypes.isEmpty() && it.returnType == CircularKSerializer::class.java }
                    ?: return null
    val result = method.invoke(instance)
    @Suppress("UNCHECKED_CAST") return result as? CircularKSerializer<T>
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun <T> Array<T>.getChecked(index: Int): T {
    return get(index)
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun BooleanArray.getChecked(index: Int): Boolean {
    return get(index)
}

internal actual fun <T : Any> KClass<T>.compiledSerializerImpl(): CircularKSerializer<T>? =
        this.constructSerializerForGivenTypeArgs()

/**
 * Creates a **strongly referenced** cache of values associated with [Class].
 * Serializers are computed using provided [factory] function.
 *
 * `null` values are not supported, though there aren't any technical limitations.
 */
internal actual fun <T> createCache(factory: (KClass<*>) -> CircularKSerializer<T>?): CircularSerializerCache<T> {
    return if (useClassValue) ClassValueCache(factory) else ConcurrentHashMapCache(factory)
}

/**
 * Creates a **strongly referenced** cache of values associated with [Class].
 * Serializers are computed using provided [factory] function.
 *
 * `null` values are not supported, though there aren't any technical limitations.
 */
internal actual fun <T> createParametrizedCache(
        factory: (KClass<Any>, List<KType>) -> CircularKSerializer<T>?): CircularParametrizedSerializerCache<T> {
    return if (useClassValue) ClassValueParametrizedCache(factory) else ConcurrentHashMapParametrizedCache(factory)
}

@Suppress("UNCHECKED_CAST")
internal actual fun <T : Any, E : T?> ArrayList<E>.toNativeArrayImpl(eClass: KClass<T>): Array<E> =
        toArray(java.lang.reflect.Array.newInstance(eClass.java, size) as Array<E>)
