package org.cirjson.serialization.internal

import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import kotlin.native.concurrent.SharedImmutable

internal const val ARRAY_NAME = "kotlin.Array"

internal const val ARRAY_LIST_NAME = "kotlin.collections.ArrayList"

internal const val LINKED_HASH_SET_NAME = "kotlin.collections.LinkedHashSet"

internal const val HASH_SET_NAME = "kotlin.collections.HashSet"

internal const val LINKED_HASH_MAP_NAME = "kotlin.collections.LinkedHashMap"

internal const val HASH_MAP_NAME = "kotlin.collections.HashMap"

@SharedImmutable
internal val EMPTY_DESCRIPTOR_ARRAY: Array<CircularSerialDescriptor> = arrayOf()
