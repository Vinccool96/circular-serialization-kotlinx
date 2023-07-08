package org.cirjson.serialization

import kotlin.reflect.KClass

/**
 *  Adds [serializerClasses] to serializers resolving process inside the plugin.
 *  Each of [serializerClasses] must implement [KSerializer].
 *
 *  Inside the file with this annotation, for each given property
 *  of type `T` in some serializable class, this list would be inspected for the presence of `KSerializer<T>`.
 *  If such serializer is present, it would be used instead of default.
 *
 *  Main use-case for this annotation is not to write @Serializable(with=SomeSerializer::class)
 *  on each property with custom serializer.
 *
 *  Serializers from this list have higher priority than default, but lesser priority than
 *  serializers defined on the property itself, such as [CircularSerializable] (with=...) or [Contextual].
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.BINARY)
public annotation class UseCircularSerializers(vararg val serializerClasses: KClass<out CircularKSerializer<*>>)