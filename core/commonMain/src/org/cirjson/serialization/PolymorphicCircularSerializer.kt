package org.cirjson.serialization

import org.cirjson.serialization.builtins.serializer
import org.cirjson.serialization.descriptors.*
import org.cirjson.serialization.internal.AbstractPolymorphicCircularSerializer
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.modules.CircularSerializersModuleBuilder
import kotlin.reflect.KClass

/**
 * This class provides support for multiplatform polymorphic serialization for interfaces and abstract classes.
 *
 * To avoid the most common security pitfalls and reflective lookup (and potential load) of an arbitrary class,
 * all serializable implementations of any polymorphic type must be [registered][CircularSerializersModuleBuilder.polymorphic]
 * in advance in the scope of base polymorphic type, efficiently preventing unbounded polymorphic serialization
 * of an arbitrary type.
 *
 * Polymorphic serialization is enabled automatically by default for interfaces and [Serializable] abstract classes.
 * To enable this feature explicitly on other types, use `@SerializableWith(PolymorphicSerializer::class)`
 * or [Polymorphic] annotation on the property.
 *
 * Usage of the polymorphic serialization can be demonstrated by the following example:
 * ```
 * abstract class BaseRequest()
 * @CircularSerializable
 * data class RequestA(val id: Int): BaseRequest()
 * @CircularSerializable
 * data class RequestB(val s: String): BaseRequest()
 *
 * abstract class BaseResponse()
 * @CircularSerializable
 * data class ResponseC(val payload: Long): BaseResponse()
 * @CircularSerializable
 * data class ResponseD(val payload: ByteArray): BaseResponse()
 *
 * @CircularSerializable
 * data class Message(
 *     @Polymorphic val request: BaseRequest,
 *     @Polymorphic val response: BaseResponse
 * )
 * ```
 * In this example, both request and response in `Message` are serializable with [PolymorphicCircularSerializer].
 *
 * `BaseRequest` and `BaseResponse` are base classes and they are captured during compile time by the plugin.
 * Yet [PolymorphicCircularSerializer] for `BaseRequest` should only allow `RequestA` and `RequestB` serializers, and none of the response's serializers.
 *
 * This is achieved via special registration function in the module:
 * ```
 * val requestAndResponseModule = SerializersModule {
 *     polymorphic(BaseRequest::class) {
 *         subclass(RequestA::class)
 *         subclass(RequestB::class)
 *     }
 *     polymorphic(BaseResponse::class) {
 *         subclass(ResponseC::class)
 *         subclass(ResponseD::class)
 *     }
 * }
 * ```
 *
 * @see CircularSerializersModule
 * @see CircularSerializersModuleBuilder.polymorphic
 */
@OptIn(InternalCircularSerializationApi::class, ExperimentalCircularSerializationApi::class)
public class PolymorphicCircularSerializer<T : Any>(override val baseClass: KClass<T>) :
        AbstractPolymorphicCircularSerializer<T>() {

    @PublishedApi // See comment in SealedClassSerializer
    internal constructor(baseClass: KClass<T>, classAnnotations: Array<Annotation>) : this(baseClass) {
        _annotations = classAnnotations.asList()
    }

    private var _annotations: List<Annotation> = emptyList()

    public override val descriptor: CircularSerialDescriptor by lazy(LazyThreadSafetyMode.PUBLICATION) {
        buildSerialDescriptor("org.cirjson.serialization.Polymorphic", PolymorphicKind.OPEN) {
            element("type", String.serializer().descriptor)
            element("value", buildSerialDescriptor("org.cirjson.serialization.Polymorphic<${baseClass.simpleName}>",
                    SerialKind.CONTEXTUAL))
            annotations = _annotations
        }.withContext(baseClass)
    }

    override fun toString(): String {
        return "org.cirjson.serialization.PolymorphicCircularSerializer(baseClass: $baseClass)"
    }

}