package org.cirjson.serialization.modules

import org.cirjson.serialization.CircularKSerializer

/** This class is needed to support re-registering the same static (argless) serializers:
 *
 * ```
 * val m1 = serializersModuleOf(A::class, A.serializer())
 * val m2 = serializersModuleOf(A::class, A.serializer())
 * val aggregate = m1 + m2 // should not throw
 * ```
 */
internal sealed class CircularContextualProvider {

    abstract operator fun invoke(typeArgumentsSerializers: List<CircularKSerializer<*>>): CircularKSerializer<*>

    class Argless(val serializer: CircularKSerializer<*>) : CircularContextualProvider() {

        override fun invoke(typeArgumentsSerializers: List<CircularKSerializer<*>>): CircularKSerializer<*> = serializer

        override fun equals(other: Any?): Boolean = other is Argless && other.serializer == this.serializer

        override fun hashCode(): Int = serializer.hashCode()
    }

    class WithTypeArguments(
            val provider: (typeArgumentsSerializers: List<CircularKSerializer<*>>) -> CircularKSerializer<*>) :
            CircularContextualProvider() {

        override fun invoke(typeArgumentsSerializers: List<CircularKSerializer<*>>): CircularKSerializer<*> =
                provider(typeArgumentsSerializers)
    }

}