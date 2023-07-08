package org.cirjson.serialization

/**
 * Indicates that property must be present during deserialization process, despite having a default value.
 */
@Target(AnnotationTarget.PROPERTY)
// @Retention(AnnotationRetention.RUNTIME) still runtime, but KT-41082
public annotation class Required