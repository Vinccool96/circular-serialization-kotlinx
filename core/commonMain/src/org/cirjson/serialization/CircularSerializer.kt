package org.cirjson.serialization

import kotlin.reflect.KClass

/**
 * Instructs the serialization plugin to turn this class into serializer for specified class [forClass].
 * However, it would not be used automatically. To apply it on particular class or property,
 * use [CircularSerializable] or [UseCircularSerializers], or [Contextual] with runtime registration.
 *
 * `@Serializer(forClass)` is experimental and unstable feature that can be changed in future releases.
 * Changes may include additional constraints on classes and objects marked with this annotation,
 * behavioural changes and even serialized shape of the class.
 *
 * @property forClass target class to create serializer for
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
@ExperimentalCircularSerializationApi
public annotation class CircularSerializer(val forClass: KClass<*>)