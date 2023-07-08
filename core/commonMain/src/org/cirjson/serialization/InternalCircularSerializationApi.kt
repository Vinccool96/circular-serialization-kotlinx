package org.cirjson.serialization

/**
 * Public API marked with this annotation is effectively **internal**, which means
 * it should not be used outside of `kotlinx.serialization`.
 * Signature, semantics, source and binary compatibilities are not guaranteed for this API
 * and will be changed without any warnings or migration aids.
 * If you cannot avoid using internal API to solve your problem, please report your use-case to serialization's issue tracker.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
public annotation class InternalCircularSerializationApi