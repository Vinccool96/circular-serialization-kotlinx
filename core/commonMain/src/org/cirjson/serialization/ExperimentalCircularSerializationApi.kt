package org.cirjson.serialization

/**
 * Marks declarations that are still **experimental** in kotlinx.serialization, which means that the design of the
 * corresponding declarations has open issues which may (or may not) lead to their changes in the future.
 * Roughly speaking, there is a chance that those declarations will be deprecated in the near future or
 * the semantics of their behavior may change in some way that may break some code.
 *
 * By default, the following categories of API are experimental:
 *
 * * Writing 3rd-party serialization formats
 * * Writing non-trivial custom serializers
 * * Implementing [SerialDescriptor] interfaces
 * * Not-yet-stable serialization formats that require additional polishing
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
public annotation class ExperimentalCircularSerializationApi