package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer

@PublishedApi
internal object UnitSerializer : CircularKSerializer<Unit> by CircularObjectSerializer("kotlin.Unit", Unit)