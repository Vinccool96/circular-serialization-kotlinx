package org.cirjson.serialization

import kotlin.reflect.KClass

/**
 * The main entry point to the serialization process.
 * Applying [CircularSerializable] to the Kotlin class instructs the serialization plugin to automatically generate implementation of [KSerializer]
 * for the current class, that can be used to serialize and deserialize the class.
 * The generated serializer can be accessed with `T.serializer()` extension function on the class companion,
 * both are generated by the plugin as well.
 *
 * ```
 * @Serializable
 * class MyData(val myData: AnotherData, val intProperty: Int, ...)
 *
 * // Produces JSON string using the generated serializer
 * val jsonString = Json.encodeToJson(MyData.serializer(), instance)
 * ```
 *
 * Additionally, the user-defined serializer can be specified using [with] parameter:
 * ```
 * @Serializable(with = MyAnotherDataCustomSerializer::class)
 * class MyAnotherData(...)
 *
 * MyAnotherData.serializer() // <- returns MyAnotherDataCustomSerializer
 * ```
 *
 * For annotated properties, specifying [with] parameter is mandatory and can be used to override
 * serializer on the use-site without affecting the rest of the usages:
 * ```
 * @Serializable // By default is serialized as 3 byte components
 * class RgbPixel(val red: Short, val green: Short, val blue: Short)
 *
 * @Serializable
 * class RgbExample(
 *     @Serializable(with = RgbAsHexString::class) p1: RgpPixel, // Serialize as HEX string, e.g. #FFFF00
 *     @Serializable(with = RgbAsSingleInt::class) p2: RgpPixel, // Serialize as single integer, e.g. 16711680
 *     p3: RgpPixel // Serialize as 3 short components, e.g. { "red": 255, "green": 255, "blue": 0 }
 * )
 * ```
 * In this example, each pixel will be serialized using different data representation.
 *
 * For classes with generic type parameters, `serializer()` function requires one additional argument per each generic type parameter:
 * ```
 * @Serializable
 * class Box<T>(value: T)
 *
 * Box.serializer() // Doesn't compile
 * Box.serializer(Int.serializer()) // Returns serializer for Box<Int>
 * Box.serializer(Box.serializer(Int.serializer())) // Returns serializer for Box<Box<Int>>
 * ```
 *
 * ### Implementation details
 *
 * In order to generate `serializer` function that is not a method on the particular instance, the class should have a companion object, either named or unnamed.
 * Companion object is generated by the plugin if it is not declared, effectively exposing both companion and `serializer()` method to class ABI.
 * If companion object already exists, only `serializer` method will be generated.
 *
 * @see UseCircularSerializers
 * @see CircularSerializer
 *
 * @property with Default value indicates that auto-generated serializer is used
 */
@MustBeDocumented
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS,
        AnnotationTarget.TYPE) //@Retention(AnnotationRetention.RUNTIME) // Runtime is the default retention, also see KT-41082
public annotation class CircularSerializable(val with: KClass<out CircularKSerializer<*>> = CircularKSerializer::class)

