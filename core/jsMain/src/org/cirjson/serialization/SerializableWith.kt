package org.cirjson.serialization

import kotlin.reflect.*

@OptIn(ExperimentalAssociatedObjects::class)
@AssociatedObjectKey
@Retention(AnnotationRetention.BINARY)
@PublishedApi
internal annotation class SerializableWith(public val serializer: KClass<out CircularKSerializer<*>>)
