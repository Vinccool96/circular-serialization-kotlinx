package org.cirjson.serialization

import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass

@OptIn(ExperimentalAssociatedObjects::class)
@AssociatedObjectKey
@Retention(AnnotationRetention.BINARY)
@PublishedApi
internal annotation class SerializableWith(public val serializer: KClass<out CircularKSerializer<*>>)
