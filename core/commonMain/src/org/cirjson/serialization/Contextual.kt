package org.cirjson.serialization

/**
 * Instructs the plugin to use [ContextualCircularSerializer] on a given property or type.
 * Context serializer is usually used when serializer for type can only be found in runtime.
 * It is also possible to apply [ContextualCircularSerializer] to every property of the given type,
 * using file-level [UseContextualCircularSerialization] annotation.
 *
 * @see ContextualCircularSerializer
 * @see UseContextualCircularSerialization
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
public annotation class Contextual