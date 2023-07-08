package org.cirjson.serialization

/**
 * Instructs the plugin to use [ContextualSerializer] on a given property or type.
 * Context serializer is usually used when serializer for type can only be found in runtime.
 * It is also possible to apply [ContextualSerializer] to every property of the given type,
 * using file-level [UseContextualCircularSerialization] annotation.
 *
 * @see ContextualSerializer
 * @see UseContextualCircularSerialization
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
public annotation class Contextual