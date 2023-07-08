package org.cirjson.serialization

import org.cirjson.serialization.internal.AbstractPolymorphicCircularSerializer
import kotlin.reflect.KClass

/**
 * This class provides support for multiplatform polymorphic serialization of sealed classes.
 *
 * In contrary to [PolymorphicCircularSerializer], all known subclasses with serializers must be passed
 * in `subclasses` and `subSerializers` constructor parameters.
 * If a subclass is a sealed class itself, all its subclasses are registered as well.
 *
 * If a sealed hierarchy is marked with [@Serializable][Serializable], an instance of this class is provided automatically.
 * In most of the cases, you won't need to perform any manual setup:
 *
 * ```
 * @Serializable
 * sealed class SimpleSealed {
 *     @Serializable
 *     public data class SubSealedA(val s: String) : SimpleSealed()
 *
 *     @Serializable
 *     public data class SubSealedB(val i: Int) : SimpleSealed()
 * }
 *
 * // will perform correct polymorphic serialization and deserialization:
 * Json.encodeToString(SimpleSealed.serializer(), SubSealedA("foo"))
 * ```
 *
 * However, it is possible to register additional subclasses using regular [CircularSerializersModule].
 * It is required when one of the subclasses is an abstract class itself:
 *
 * ```
 * @Serializable
 * sealed class ProtocolWithAbstractClass {
 *     @Serializable
 *     abstract class Message : ProtocolWithAbstractClass() {
 *         @Serializable
 *         data class StringMessage(val description: String, val message: String) : Message()
 *
 *         @Serializable
 *         data class IntMessage(val description: String, val message: Int) : Message()
 *     }
 *
 *     @Serializable
 *     data class ErrorMessage(val error: String) : ProtocolWithAbstractClass()
 * }
 * ```
 *
 * In this case, `ErrorMessage` would be registered automatically by the plugin,
 * but `StringMessage` and `IntMessage` require manual registration, as described in [PolymorphicCircularSerializer] documentation:
 *
 * ```
 * val abstractContext = SerializersModule {
 *     polymorphic(ProtocolWithAbstractClass::class) {
 *         subclass(ProtocolWithAbstractClass.Message.IntMessage::class)
 *         subclass(ProtocolWithAbstractClass.Message.StringMessage::class)
 *         // no need to register ProtocolWithAbstractClass.ErrorMessage
 *     }
 * }
 * ```
 */
@InternalCircularSerializationApi
@OptIn(ExperimentalCircularSerializationApi::class)
public class SealedClassCircularSerializer<T : Any>(serialName: String, override val baseClass: KClass<T>,
        subclasses: Array<KClass<out T>>, subclassSerializers: Array<CircularKSerializer<out T>>) :
        AbstractPolymorphicCircularSerializer<T>() {

    /**
     * This constructor is needed to store serial info annotations defined on the sealed class.
     * Support for such annotations was added in Kotlin 1.5.30; previous plugins used primary constructor of this class
     * directly, therefore this constructor is secondary.
     *
     * This constructor can (and should) became primary when Require-Kotlin-Version is raised to at least 1.5.30
     * to remove necessity to store annotations separately and calculate descriptor via `lazy {}`.
     *
     * When doing this change, also migrate secondary constructors from [PolymorphicCircularSerializer] and [ObjectSerializer].
     */
    @PublishedApi
    internal constructor(serialName: String, baseClass: KClass<T>, subclasses: Array<KClass<out T>>,
            subclassSerializers: Array<CircularKSerializer<out T>>, classAnnotations: Array<Annotation>) : this(
            serialName, baseClass, subclasses, subclassSerializers) {
        this._annotations = classAnnotations.asList()
    }

    private var _annotations: List<Annotation> = emptyList()

    override val descriptor: CircularSerialDescriptor by lazy(LazyThreadSafetyMode.PUBLICATION) {
        buildSerialDescriptor(serialName, PolymorphicKind.SEALED) {
            element("type", String.serializer().descriptor)
            val elementDescriptor = buildSerialDescriptor("kotlinx.serialization.Sealed<${baseClass.simpleName}>",
                    SerialKind.CONTEXTUAL) { // serialName2Serializer is guaranteed to have no duplicates — checked in `init`.
                serialName2Serializer.forEach { (name, serializer) ->
                    element(name, serializer.descriptor)
                }
            }
            element("value", elementDescriptor)
            annotations = _annotations
        }
    }

    private val class2Serializer: Map<KClass<out T>, CircularKSerializer<out T>>
    private val serialName2Serializer: Map<String, CircularKSerializer<out T>>

    init {
        if (subclasses.size != subclassSerializers.size) {
            throw IllegalArgumentException(
                    "All subclasses of sealed class ${baseClass.simpleName} should be marked @Serializable")
        }

        // Note: we do not check whether different serializers are provided if the same KClass duplicated in the `subclasses`.
        // Plugin should produce identical serializers, although they are not always strictly equal (e.g. new ObjectSerializer
        // may be created every time)
        class2Serializer = subclasses.zip(subclassSerializers).toMap()
        serialName2Serializer = class2Serializer.entries.groupingBy { it.value.descriptor.serialName }
            .aggregate<Map.Entry<KClass<out T>, CircularKSerializer<out T>>, String, Map.Entry<KClass<*>, CircularKSerializer<out T>>> { key, accumulator, element, _ ->
                if (accumulator != null) {
                    error("Multiple sealed subclasses of '$baseClass' have the same serial name '$key':" + " '${accumulator.key}', '${element.key}'")
                }
                element
            }.mapValues { it.value.value }
    }

    override fun findPolymorphicSerializerOrNull(decoder: CompositeDecoder,
            klassName: String?): CircularDeserializationStrategy<T>? {
        return serialName2Serializer[klassName] ?: super.findPolymorphicSerializerOrNull(decoder, klassName)
    }

    override fun findPolymorphicSerializerOrNull(encoder: Encoder, value: T): CircularSerializationStrategy<T>? {
        return (class2Serializer[value::class] ?: super.findPolymorphicSerializerOrNull(encoder, value))?.cast()
    }
}