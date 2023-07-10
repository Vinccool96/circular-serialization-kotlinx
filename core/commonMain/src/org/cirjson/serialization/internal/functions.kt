package org.cirjson.serialization.internal

import org.cirjson.serialization.*
import org.cirjson.serialization.descriptors.*
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import kotlin.jvm.JvmName
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter

@JvmName("throwSubtypeNotRegistered")
internal fun throwSubtypeNotRegistered(subClassName: String?, baseClass: KClass<*>): Nothing {
    val scope = "in the scope of '${baseClass.simpleName}'"
    throw CircularSerializationException(
            if (subClassName == null) "Class discriminator was missing and no default polymorphic serializers were registered $scope"
            else "Class '$subClassName' is not registered for polymorphic serialization $scope.\n" + "To be registered automatically, class '$subClassName' has to be '@Serializable', and the base class '${baseClass.simpleName}' has to be sealed and '@Serializable'.\n" + "Alternatively, register the serializer for '$subClassName' explicitly in a corresponding SerializersModule.")
}

@JvmName("throwSubtypeNotRegistered")
internal fun throwSubtypeNotRegistered(subClass: KClass<*>, baseClass: KClass<*>): Nothing =
        throwSubtypeNotRegistered(subClass.simpleName ?: "$subClass", baseClass)

@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
internal fun <T : Enum<T>> createSimpleEnumSerializer(serialName: String, values: Array<T>): CircularKSerializer<T> {
    return CircularEnumSerializer(serialName, values)
}

/**
 * The function has a bug (#2121) and should not be used by new (1.8.20+) plugins. It is preserved for backward compatibility with previously compiled enum classes.
 */
@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
internal fun <T : Enum<T>> createMarkedEnumSerializer(serialName: String, values: Array<T>, names: Array<String?>,
        annotations: Array<Array<Annotation>?>): CircularKSerializer<T> {
    val descriptor = CircularEnumDescriptor(serialName, values.size)
    values.forEachIndexed { i, v ->
        val elementName = names.getOrNull(i) ?: v.name
        descriptor.addElement(elementName)
        annotations.getOrNull(i)?.forEach {
            descriptor.pushAnnotation(it)
        }
    }

    return CircularEnumSerializer(serialName, values, descriptor)
}

@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
internal fun <T : Enum<T>> createAnnotatedEnumSerializer(serialName: String, values: Array<T>, names: Array<String?>,
        entryAnnotations: Array<Array<Annotation>?>, classAnnotations: Array<Annotation>?): CircularKSerializer<T> {
    val descriptor = CircularEnumDescriptor(serialName, values.size)
    classAnnotations?.forEach {
        descriptor.pushClassAnnotation(it)
    }
    values.forEachIndexed { i, v ->
        val elementName = names.getOrNull(i) ?: v.name
        descriptor.addElement(elementName)
        entryAnnotations.getOrNull(i)?.forEach {
            descriptor.pushAnnotation(it)
        }
    }

    return CircularEnumSerializer(serialName, values, descriptor)
}

@OptIn(ExperimentalCircularSerializationApi::class)
internal inline fun <reified SD : CircularSerialDescriptor> SD.equalsImpl(other: Any?,
        typeParamsAreEqual: (otherDescriptor: SD) -> Boolean): Boolean {
    if (this === other) return true
    if (other !is SD) return false
    if (serialName != other.serialName) return false
    if (!typeParamsAreEqual(other)) return false
    if (this.elementsCount != other.elementsCount) return false
    for (index in 0..<elementsCount) {
        if (getElementDescriptor(index).serialName != other.getElementDescriptor(index).serialName) return false
        if (getElementDescriptor(index).kind != other.getElementDescriptor(index).kind) return false
    }
    return true
}

@OptIn(ExperimentalCircularSerializationApi::class)
internal fun CircularSerialDescriptor.hashCodeImpl(typeParams: Array<CircularSerialDescriptor>): Int {
    var result = serialName.hashCode()
    result = 31 * result + typeParams.contentHashCode()
    val elementDescriptors = elementDescriptors
    val namesHash = elementDescriptors.elementsHashCodeBy { it.serialName }
    val kindHash = elementDescriptors.elementsHashCodeBy { it.kind }
    result = 31 * result + namesHash
    result = 31 * result + kindHash
    return result
}

@InternalCircularSerializationApi
public fun <T> InlineCircularPrimitiveDescriptor(name: String,
        primitiveSerializer: CircularKSerializer<T>): CircularSerialDescriptor =
        InlineClassDescriptor(name, object : GeneratedCircularSerializer<T> {

            // object needed only to pass childSerializers()
            override fun childSerializers(): Array<CircularKSerializer<*>> = arrayOf(primitiveSerializer)

            override val descriptor: CircularSerialDescriptor get() = error("unsupported")

            override fun serialize(encoder: CircularEncoder, value: T) {
                error("unsupported")
            }

            override fun deserialize(decoder: CircularDecoder): T {
                error("unsupported")
            }

        })

@OptIn(ExperimentalCircularSerializationApi::class)
internal fun CircularSerialDescriptor.cachedSerialNames(): Set<String> {
    if (this is CircularCachedNames) return serialNames
    val result = HashSet<String>(elementsCount)
    for (i in 0..<elementsCount) {
        result += getElementName(i)
    }
    return result
}

/**
 * Same as [toTypedArray], but uses special empty array constant, if [this]
 * is null or empty.
 */
internal fun List<CircularSerialDescriptor>?.compactArray(): Array<CircularSerialDescriptor> =
        takeUnless { it.isNullOrEmpty() }?.toTypedArray() ?: EMPTY_DESCRIPTOR_ARRAY

@PublishedApi
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
internal inline fun <T> CircularKSerializer<*>.cast(): CircularKSerializer<T> = this as CircularKSerializer<T>

@PublishedApi
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
internal inline fun <T> CircularSerializationStrategy<*>.cast(): CircularSerializationStrategy<T> =
        this as CircularSerializationStrategy<T>

@PublishedApi
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
internal inline fun <T> CircularDeserializationStrategy<*>.cast(): CircularDeserializationStrategy<T> =
        this as CircularDeserializationStrategy<T>

internal fun KClass<*>.serializerNotRegistered(): Nothing {
    throw CircularSerializationException(notRegisteredMessage())
}

internal fun KClass<*>.notRegisteredMessage(): String =
        notRegisteredMessage(simpleName ?: "<local class name not available>")

internal fun notRegisteredMessage(className: String): String =
        "Serializer for class '$className' is not found.\nPlease ensure that class is marked as '@Serializable' and that the serialization compiler plugin is applied.\n"

internal expect fun KClass<*>.platformSpecificSerializerNotRegistered(): Nothing

@Suppress("UNCHECKED_CAST")
internal fun KType.kclass() = when (val t = classifier) {
    is KClass<*> -> t
    is KTypeParameter -> {
        error("Captured type parameter $t from generic non-reified function. Such functionality cannot be supported as $t is erased, either specify serializer explicitly or make calling function inline with reified $t")
    }

    else -> error("Only KClass supported as classifier, got $t")
} as KClass<Any>

/**
 * Constructs KSerializer<D<T0, T1, ...>> by given KSerializer<T0>, KSerializer<T1>, ...
 * via reflection (on JVM) or compiler+plugin intrinsic `SerializerFactory` (on Native)
 */
internal expect fun <T : Any> KClass<T>.constructSerializerForGivenTypeArgs(
        vararg args: CircularKSerializer<Any?>): CircularKSerializer<T>?

/**
 * Checks whether given KType and its corresponding KClass represent a reference array
 */
internal expect fun isReferenceArray(rootClass: KClass<Any>): Boolean

/**
 *  Array.get that checks indices on JS
 */
internal expect fun <T> Array<T>.getChecked(index: Int): T

/**
 *  Array.get that checks indices on JS
 */
internal expect fun BooleanArray.getChecked(index: Int): Boolean

internal expect fun <T : Any> KClass<T>.compiledSerializerImpl(): CircularKSerializer<T>?

/**
 * Create serializers cache for non-parametrized and non-contextual serializers.
 * The activity and type of cache is determined for a specific platform and a specific environment.
 */
internal expect fun <T> createCache(factory: (KClass<*>) -> CircularKSerializer<T>?): CircularSerializerCache<T>

/**
 * Create serializers cache for parametrized and non-contextual serializers. Parameters also non-contextual.
 * The activity and type of cache is determined for a specific platform and a specific environment.
 */
internal expect fun <T> createParametrizedCache(
        factory: (KClass<Any>, List<KType>) -> CircularKSerializer<T>?): CircularParametrizedSerializerCache<T>

internal expect fun <T : Any, E : T?> ArrayList<E>.toNativeArrayImpl(eClass: KClass<T>): Array<E>

internal inline fun <T, K> Iterable<T>.elementsHashCodeBy(selector: (T) -> K): Int {
    return fold(1) { hash, element -> 31 * hash + selector(element).hashCode() }
}

@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
public fun throwMissingFieldException(seen: Int, goldenMask: Int, descriptor: CircularSerialDescriptor) {
    val missingFields = mutableListOf<String>()

    var missingFieldsBits = goldenMask and seen.inv()
    for (i in 0..<32) {
        if (missingFieldsBits and 1 != 0) {
            missingFields += descriptor.getElementName(i)
        }
        missingFieldsBits = missingFieldsBits ushr 1
    }
    throw MissingFieldException(missingFields, descriptor.serialName)
}

@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
public fun throwArrayMissingFieldException(seenArray: IntArray, goldenMaskArray: IntArray,
        descriptor: CircularSerialDescriptor) {
    val missingFields = mutableListOf<String>()

    for (maskSlot in goldenMaskArray.indices) {
        var missingFieldsBits = goldenMaskArray[maskSlot] and seenArray[maskSlot].inv()
        if (missingFieldsBits != 0) {
            for (i in 0..<32) {
                if (missingFieldsBits and 1 != 0) {
                    missingFields += descriptor.getElementName(maskSlot * 32 + i)
                }
                missingFieldsBits = missingFieldsBits ushr 1
            }
        }
    }
    throw MissingFieldException(missingFields, descriptor.serialName)
}

internal fun PrimitiveDescriptorSafe(serialName: String, kind: PrimitiveKind): CircularSerialDescriptor {
    checkName(serialName)
    return PrimitiveSerialDescriptor(serialName, kind)
}

private fun checkName(serialName: String) {
    val keys = BUILTIN_SERIALIZERS.keys
    for (primitive in keys) {
        val simpleName = primitive.simpleName!!.capitalize()
        val qualifiedName = "kotlin.$simpleName" // KClass.qualifiedName is not supported in JS
        if (serialName.equals(qualifiedName, ignoreCase = true) || serialName.equals(simpleName, ignoreCase = true)) {
            throw IllegalArgumentException("""
                The name of serial descriptor should uniquely identify associated serializer.
                For serial name $serialName there already exist ${simpleName.capitalize()}Serializer.
                Please refer to SerialDescriptor documentation for additional information.
            """.trimIndent())
        }
    }
}

private fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
