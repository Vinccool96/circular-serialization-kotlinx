package org.cirjson.serialization

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
@OptIn(ExperimentalCircularSerializationApi::class)
annotation class Id(val id: Int)