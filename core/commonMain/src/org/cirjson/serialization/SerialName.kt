package org.cirjson.serialization

/**
 * Overrides the name of a class or a property in the corresponding [SerialDescriptor].
 * Names and serial names are used by text-based serial formats in order to encode the name of the class or
 * the name of the property, e.g. by `Json`.
 *
 * By default, [SerialDescriptor.serialName] and [SerialDescriptor.getElementName]
 * are associated with fully-qualified name of the target class and the name of the property respectively.
 * Applying this annotation changes the visible name to the given [value]:
 *
 * ```
 * package foo
 *
 * @Serializable // RegularName.serializer().descriptor.serialName is "foo.RegularName"
 * class RegularName(val myInt: Int)
 *
 * @Serializable
 * @SerialName("CustomName") // Omit package from name that is used for diagnostic and polymorphism
 * class CustomName(@SerialName("int") val myInt: Int)
 *
 * // Prints "{"myInt":42}"
 * println(Json.encodeToString(RegularName(42)))
 * // Prints "{"int":42}"
 * println(Json.encodeToString(CustomName(42)))
 * ```
 *
 * If a name of class or property is overridden with this annotation, original source code name is not available for the library.
 * Tools like `JsonNamingStrategy` and `ProtoBufSchemaGenerator` would see and transform [value] from [SerialName] annotation.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
// @Retention(AnnotationRetention.RUNTIME) still runtime, but KT-41082
public annotation class SerialName(val value: String)