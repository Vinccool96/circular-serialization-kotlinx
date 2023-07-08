package org.cirjson.serialization

/**
 * Meta-annotation that commands the compiler plugin to handle the annotation as serialization-specific.
 * Serialization-specific annotations are preserved in the [SerialDescriptor] and can be retrieved
 * during serialization process with [SerialDescriptor.getElementAnnotations] for properties annotations
 * and [SerialDescriptor.annotations] for class annotations.
 *
 * It is recommended to explicitly specify target for serial info annotations, whether it is [AnnotationTarget.PROPERTY], [AnnotationTarget.CLASS], or both.
 * Keep in mind that Kotlin compiler prioritizes [function parameter target][AnnotationTarget.VALUE_PARAMETER] over [property target][AnnotationTarget.PROPERTY],
 * so serial info annotations used on constructor-parameters-as-properties without explicit declaration-site or use-site target are not preserved.
 */
@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@ExperimentalCircularSerializationApi
public annotation class SerialInfo