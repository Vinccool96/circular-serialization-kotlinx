package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.internal.CircularArrayListClassDesc
import org.cirjson.serialization.internal.CircularHashMapClassDesc
import org.cirjson.serialization.internal.CircularHashSetClassDesc
import org.cirjson.serialization.modules.CircularSerialModuleImpl
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.modules.CircularSerializersModuleBuilder
import org.cirjson.serialization.modules.PolymorphicCircularModuleBuilder
import org.cirjson.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Looks up a descriptor of serializer registered for contextual serialization in [this],
 * using [CircularSerialDescriptor.capturedKClass] as a key.
 *
 * @see CircularSerializersModuleBuilder.contextual
 */
@ExperimentalCircularSerializationApi
public fun CircularSerializersModule.getContextualDescriptor(
        descriptor: CircularSerialDescriptor): CircularSerialDescriptor? =
        descriptor.capturedKClass?.let { klass -> getContextual(klass)?.descriptor }

/**
 * Retrieves a collection of descriptors which serializers are registered for polymorphic serialization in [this]
 * with base class equal to [descriptor]'s [CircularSerialDescriptor.capturedKClass].
 * This method does not retrieve serializers registered with [PolymorphicCircularModuleBuilder.defaultDeserializer]
 * or [PolymorphicCircularModuleBuilder.defaultSerializer].
 *
 * @see CircularSerializersModule.getPolymorphic
 * @see CircularSerializersModuleBuilder.polymorphic
 */
@ExperimentalCircularSerializationApi
public fun CircularSerializersModule.getPolymorphicDescriptors(
        descriptor: CircularSerialDescriptor): List<CircularSerialDescriptor> {
    val kClass = descriptor.capturedKClass ?: return emptyList()
    // SerializersModule is sealed class with the only implementation
    return (this as CircularSerialModuleImpl).polyBase2Serializers[kClass]?.values.orEmpty().map { it.descriptor }
}

/**
 * Wraps [this] in [CircularContextDescriptor].
 */
internal fun CircularSerialDescriptor.withContext(context: KClass<*>): CircularSerialDescriptor =
        CircularContextDescriptor(this, context)

/**
 * Builder for [CircularSerialDescriptor].
 * The resulting descriptor will be uniquely identified by the given [serialName], [typeParameters] and
 * elements structure described in [builderAction] function.
 *
 * Example:
 * ```
 * // Class with custom serializer and custom serial descriptor
 * class Data(
 *     val intField: Int, // This field is ignored by custom serializer
 *     val longField: Long, // This field is written as long, but in serialized form is named as "_longField"
 *     val stringList: List<String> // This field is written as regular list of strings
 *     val nullableInt: Int?
 * )
 * // Descriptor for such class:
 * buildClassSerialDescriptor("my.package.Data") {
 *     // intField is deliberately ignored by serializer -- not present in the descriptor as well
 *     element<Long>("_longField") // longField is named as _longField
 *     element("stringField", listSerialDescriptor<String>()) // or ListSerializer(String.serializer()).descriptor
 *     element("nullableInt", serialDescriptor<Int>().nullable)
 * }
 * ```
 *
 * Example for generic classes:
 * ```
 * import kotlinx.serialization.builtins.*
 *
 * @Serializable(CustomSerializer::class)
 * class BoxedList<T>(val list: List<T>)
 *
 * class CustomSerializer<T>(tSerializer: KSerializer<T>): KSerializer<BoxedList<T>> {
 *   // here we use tSerializer.descriptor because it represents T
 *   override val descriptor = buildClassSerialDescriptor("pkg.BoxedList", tSerializer.descriptor) {
 *     // here we have to wrap it with List first, because property has type List<T>
 *     element("list", ListSerializer(tSerializer).descriptor) // or listSerialDescriptor(tSerializer.descriptor)
 *   }
 * }
 * ```
 */
@Suppress("FunctionName")
@OptIn(ExperimentalCircularSerializationApi::class)
public fun buildClassSerialDescriptor(serialName: String, vararg typeParameters: CircularSerialDescriptor,
        builderAction: CircularClassSerialDescriptorBuilder.() -> Unit = {}): CircularSerialDescriptor {
    require(serialName.isNotBlank()) { "Blank serial names are prohibited" }
    val sdBuilder = CircularClassSerialDescriptorBuilder(serialName)
    sdBuilder.builderAction()
    return CircularSerialDescriptorImpl(serialName, StructureKind.CLASS, sdBuilder.elementNames.size,
            typeParameters.toList(), sdBuilder)
}

/**
 * Factory to create a trivial primitive descriptors.
 * Primitive descriptors should be used when the serialized form of the data has a primitive form, for example:
 * ```
 * object LongAsStringSerializer : KSerializer<Long> {
 *     override val descriptor: SerialDescriptor =
 *         PrimitiveSerialDescriptor("kotlinx.serialization.LongAsStringSerializer", PrimitiveKind.STRING)
 *
 *     override fun serialize(encoder: Encoder, value: Long) {
 *         encoder.encodeString(value.toString())
 *     }
 *
 *     override fun deserialize(decoder: Decoder): Long {
 *         return decoder.decodeString().toLong()
 *     }
 * }
 * ```
 */
public fun PrimitiveSerialDescriptor(serialName: String, kind: PrimitiveKind): CircularSerialDescriptor {
    require(serialName.isNotBlank()) { "Blank serial names are prohibited" }
    return PrimitiveDescriptorSafe(serialName, kind)
}

/**
 * Factory to create a new descriptor that is identical to [original] except that the name is equal to [serialName].
 * Should be used when you want to serialize a type as another non-primitive type.
 * Don't use this if you want to serialize a type as a primitive value, use [PrimitiveSerialDescriptor] instead.
 *
 * Example:
 * ```
 * @Serializable(CustomSerializer::class)
 * class CustomType(val a: Int, val b: Int, val c: Int)
 *
 * class CustomSerializer: KSerializer<CustomType> {
 *     override val descriptor = SerialDescriptor("CustomType", IntArraySerializer().descriptor)
 *
 *     override fun serialize(encoder: Encoder, value: CustomType) {
 *         encoder.encodeSerializableValue(IntArraySerializer(), intArrayOf(value.a, value.b, value.c))
 *     }
 *
 *     override fun deserialize(decoder: Decoder): CustomType {
 *         val array = decoder.decodeSerializableValue(IntArraySerializer())
 *         return CustomType(array[0], array[1], array[2])
 *     }
 * }
 * ```
 */
@ExperimentalCircularSerializationApi
public fun SerialDescriptor(serialName: String, original: CircularSerialDescriptor): CircularSerialDescriptor {
    require(serialName.isNotBlank()) { "Blank serial names are prohibited" }
    require(original.kind !is PrimitiveKind) { "For primitive descriptors please use 'PrimitiveSerialDescriptor' instead" }
    require(serialName != original.serialName) { "The name of the wrapped descriptor ($serialName) cannot be the same as the name of the original descriptor (${original.serialName})" }

    return WrappedCircularSerialDescriptor(serialName, original)
}

/**
 * An unsafe alternative to [buildClassSerialDescriptor] that supports an arbitrary [SerialKind].
 * This function is left public only for migration of pre-release users and is not intended to be used
 * as generally-safe and stable mechanism. Beware that it can produce inconsistent or non spec-compliant instances.
 *
 * If you end up using this builder, please file an issue with your use-case in kotlinx.serialization issue tracker.
 */
@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
public fun buildSerialDescriptor(serialName: String, kind: SerialKind, vararg typeParameters: CircularSerialDescriptor,
        builder: CircularClassSerialDescriptorBuilder.() -> Unit = {}): CircularSerialDescriptor {
    require(serialName.isNotBlank()) { "Blank serial names are prohibited" }
    require(kind != StructureKind.CLASS) { "For StructureKind.CLASS please use 'buildClassSerialDescriptor' instead" }
    val sdBuilder = CircularClassSerialDescriptorBuilder(serialName)
    sdBuilder.builder()
    return CircularSerialDescriptorImpl(serialName, kind, sdBuilder.elementNames.size, typeParameters.toList(),
            sdBuilder)
}


/**
 * Retrieves descriptor of type [T] using reified [serializer] function.
 */
public inline fun <reified T> serialDescriptor(): CircularSerialDescriptor = serializer<T>().descriptor

/**
 * Retrieves descriptor of type associated with the given [KType][type]
 */
public fun serialDescriptor(type: KType): CircularSerialDescriptor = serializer(type).descriptor

/**
 * Creates a descriptor for the type `List<T>` where `T` is the type associated with [elementDescriptor].
 */
@ExperimentalCircularSerializationApi
public fun listSerialDescriptor(elementDescriptor: CircularSerialDescriptor): CircularSerialDescriptor {
    return CircularArrayListClassDesc(elementDescriptor)
}

/**
 * Creates a descriptor for the type `List<T>`.
 */
@ExperimentalCircularSerializationApi
public inline fun <reified T> listSerialDescriptor(): CircularSerialDescriptor {
    return listSerialDescriptor(serializer<T>().descriptor)
}

/**
 * Creates a descriptor for the type `Map<K, V>` where `K` and `V` are types
 * associated with [keyDescriptor] and [valueDescriptor] respectively.
 */
@ExperimentalCircularSerializationApi
public fun mapSerialDescriptor(keyDescriptor: CircularSerialDescriptor,
        valueDescriptor: CircularSerialDescriptor): CircularSerialDescriptor {
    return CircularHashMapClassDesc(keyDescriptor, valueDescriptor)
}

/**
 * Creates a descriptor for the type `Map<K, V>`.
 */
@ExperimentalCircularSerializationApi
public inline fun <reified K, reified V> mapSerialDescriptor(): CircularSerialDescriptor {
    return mapSerialDescriptor(serializer<K>().descriptor, serializer<V>().descriptor)
}

/**
 * Creates a descriptor for the type `Set<T>` where `T` is the type associated with [elementDescriptor].
 */
@ExperimentalCircularSerializationApi
public fun setSerialDescriptor(elementDescriptor: CircularSerialDescriptor): CircularSerialDescriptor {
    return CircularHashSetClassDesc(elementDescriptor)
}

/**
 * Creates a descriptor for the type `Set<T>`.
 */
@ExperimentalCircularSerializationApi
public inline fun <reified T> setSerialDescriptor(): CircularSerialDescriptor {
    return setSerialDescriptor(serializer<T>().descriptor)
}

/**
 * A reified version of [element] function that
 * extract descriptor using `serializer<T>().descriptor` call with all the restrictions of `serializer<T>().descriptor`.
 */
public inline fun <reified T> CircularClassSerialDescriptorBuilder.element(elementName: String,
        annotations: List<Annotation> = emptyList(), isOptional: Boolean = false) {
    val descriptor = serializer<T>().descriptor
    element(elementName, descriptor, annotations, isOptional)
}