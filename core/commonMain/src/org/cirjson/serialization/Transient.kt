package org.cirjson.serialization

/**
 * Marks this property invisible for the whole serialization process, including [serial descriptors][SerialDescriptor].
 * Transient properties must have default values.
 */
@Target(AnnotationTarget.PROPERTY)
// @Retention(AnnotationRetention.RUNTIME) still runtime, but KT-41082
public annotation class Transient