package org.cirjson.serialization

import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.SerialKind
import org.cirjson.serialization.descriptors.buildSerialDescriptor
import org.cirjson.serialization.descriptors.withContext
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import org.cirjson.serialization.internal.EMPTY_SERIALIZER_ARRAY
import org.cirjson.serialization.internal.serializerNotRegistered
import kotlin.reflect.KClass
import org.cirjson.serialization.modules.CircularSerializersModule

/**
 * This class provides support for retrieving a serializer in runtime, instead of using the one precompiled by the serialization plugin.
 * This serializer is enabled by [Contextual] or [UseContextualSerialization].
 *
 * Typical usage of `ContextualSerializer` would be a serialization of a class which does not have
 * static serializer (e.g. Java class or class from 3rd party library);
 * or desire to override serialized class form in one dedicated output format.
 *
 * Serializers are being looked for in a [CircularSerializersModule] from the target [CircularEncoder] or [CircularDecoder], using statically known [KClass].
 * To create a serial module, use [CircularSerializersModule] factory function.
 * To pass it to encoder and decoder, refer to particular [SerialFormat]'s documentation.
 *
 * Usage of contextual serializer can be demonstrated by the following example:
 * ```
 * import java.util.Date
 *
 * @Serializable
 * class ClassWithDate(val data: String, @Contextual val timestamp: Date)
 *
 * val moduleForDate = serializersModule(MyISO8601DateSerializer)
 * val json = Json { serializersModule = moduleForDate }
 * json.encodeToString(ClassWithDate("foo", Date())
 * ```
 *
 * If type of the property marked with `@Contextual` is `@Serializable` by itself, the plugin-generated serializer is
 * used as a fallback if no serializers associated with a given type is registered in the module.
 * The fallback serializer is determined by the static type of the property, not by its actual type.
 */
@ExperimentalCircularSerializationApi
@OptIn(InternalCircularSerializationApi::class)
public class ContextualCircularSerializer<T : Any>(private val serializableClass: KClass<T>,
        private val fallbackSerializer: CircularKSerializer<T>?,
        typeArgumentsSerializers: Array<CircularKSerializer<*>>) : CircularKSerializer<T> {

    private val typeArgumentsSerializers: List<CircularKSerializer<*>> = typeArgumentsSerializers.asList()

    private fun serializer(serializersModule: CircularSerializersModule): CircularKSerializer<T> =
            serializersModule.getContextual(serializableClass, typeArgumentsSerializers) ?: fallbackSerializer
            ?: serializableClass.serializerNotRegistered()

    // Used from the old plugins
    @Suppress("unused")
    public constructor(serializableClass: KClass<T>) : this(serializableClass, null, EMPTY_SERIALIZER_ARRAY)

    public override val descriptor: CircularSerialDescriptor =
            buildSerialDescriptor("org.cirjson.serialization.ContextualCircularSerializer", SerialKind.CONTEXTUAL) {
                annotations = fallbackSerializer?.descriptor?.annotations.orEmpty()
            }.withContext(serializableClass)

    public override fun serialize(encoder: CircularEncoder, value: T) {
        encoder.encodeSerializableValue(serializer(encoder.serializersModule), value)
    }

    public override fun deserialize(decoder: CircularDecoder): T {
        return decoder.decodeSerializableValue(serializer(decoder.serializersModule))
    }

}