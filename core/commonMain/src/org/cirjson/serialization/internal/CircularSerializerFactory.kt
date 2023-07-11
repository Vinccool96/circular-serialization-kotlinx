package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializable

/**
 * An internal interface used by the compiler plugin for objects that are factories of typed serializers, for example
 * for auto-generated companion objects for [CircularSerializable] classes with type parameters.
 *
 * This interface is used to lookup and create serializers in K/N using `@AssociatedObjectKey`.
 * Should not be used in any user code. Please use generated `.serializer(kSerializer1, kSerializer2, ...)`
 * method on a companion or top-level `serializer(KType)` function.
 */
@Deprecated("Inserted into generated code and should not be used directly", level = DeprecationLevel.HIDDEN)
public interface CircularSerializerFactory {

    public fun serializer(vararg typeParamsSerializers: CircularKSerializer<*>): CircularKSerializer<*>

}