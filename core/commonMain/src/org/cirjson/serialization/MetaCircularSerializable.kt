package org.cirjson.serialization

/**
 * The meta-annotation for adding [CircularSerializable] behaviour to user-defined annotations.
 *
 * Applying [MetaCircularSerializable] to the annotation class `A` instructs the serialization plugin to treat annotation A
 * as [CircularSerializable]. In addition, all annotations marked with [MetaCircularSerializable] are saved in the generated [SerialDescriptor]
 * as if they are annotated with [SerialInfo].
 *
 * ```
 * @MetaSerializable
 * @Target(AnnotationTarget.CLASS)
 * annotation class MySerializable(val data: String)
 *
 * @MySerializable("some_data")
 * class MyData(val myData: AnotherData, val intProperty: Int, ...)
 *
 * val serializer = MyData.serializer()
 * serializer.descriptor.annotations.filterIsInstance<MySerializable>().first().data // <- returns "some_data"
 * ```
 *
 * @see CircularSerializable
 * @see SerialInfo
 * @see UseCircularSerializers
 * @see CircularSerializer
 */
@MustBeDocumented
@Target(AnnotationTarget.ANNOTATION_CLASS)
//@Retention(AnnotationRetention.RUNTIME) // Runtime is the default retention, also see KT-41082
@ExperimentalCircularSerializationApi
public annotation class MetaCircularSerializable