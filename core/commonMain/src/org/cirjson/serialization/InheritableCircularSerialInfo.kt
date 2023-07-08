package org.cirjson.serialization

/**
 * Meta-annotation that commands the compiler plugin to handle the annotation as serialization-specific.
 * Serialization-specific annotations are preserved in the [SerialDescriptor] and can be retrieved
 * during serialization process with [SerialDescriptor.getElementAnnotations].
 *
 * In contrary to regular [SerialInfo], this one makes annotations inheritable:
 * If class X marked as [CircularSerializable] has any of its supertypes annotated with annotation A that has `@InheritableSerialInfo` on it,
 * A appears in X's [SerialDescriptor] even if X itself is not annotated.
 * It is possible to use A multiple times on different supertypes. Resulting X's [SerialDescriptor.annotations] would still contain
 * only one instance of A.
 * Note that if A has any arguments, their values should be the same across all hierarchy. Otherwise, a compilation error
 * would be reported by the plugin.
 *
 * Example:
 * ```
 * @InheritableCircularSerialInfo
 * annotation class A(val value: Int)
 *
 * @A(1) // Annotation can also be inherited from interfaces
 * interface I
 *
 * @Serializable
 * @A(1) // Argument value is the same as in I, no compiler error
 * abstract class Base: I
 *
 * @Serializable
 * class Derived: Base()
 *
 * // This function returns 1.
 * fun foo(): Int = Derived.serializer().descriptor.annotations.filterIsInstance<A>().single().value
 * ```
 */
@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@ExperimentalCircularSerializationApi
public annotation class InheritableCircularSerialInfo