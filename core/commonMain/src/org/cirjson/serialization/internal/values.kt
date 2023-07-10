@file:OptIn(ExperimentalCircularSerializationApi::class)

package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.builtins.*
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import kotlin.jvm.JvmField
import kotlin.native.concurrent.SharedImmutable
import kotlin.time.Duration

internal const val ARRAY_NAME = "kotlin.Array"

internal const val ARRAY_LIST_NAME = "kotlin.collections.ArrayList"

internal const val LINKED_HASH_SET_NAME = "kotlin.collections.LinkedHashSet"

internal const val HASH_SET_NAME = "kotlin.collections.HashSet"

internal const val LINKED_HASH_MAP_NAME = "kotlin.collections.LinkedHashMap"

internal const val HASH_MAP_NAME = "kotlin.collections.HashMap"

@SharedImmutable
internal val EMPTY_DESCRIPTOR_ARRAY: Array<CircularSerialDescriptor> = arrayOf()

@SharedImmutable
@JvmField
internal val EMPTY_SERIALIZER_ARRAY: Array<CircularKSerializer<*>> = arrayOf()

internal const val INITIAL_SIZE = 10

@SharedImmutable
@OptIn(ExperimentalUnsignedTypes::class)
internal val BUILTIN_SERIALIZERS = mapOf(String::class to String.serializer(), Char::class to Char.serializer(),
        CharArray::class to CircularCharArraySerializer(), Double::class to Double.serializer(),
        DoubleArray::class to CircularDoubleArraySerializer(), Float::class to Float.serializer(),
        FloatArray::class to CircularFloatArraySerializer(), Long::class to Long.serializer(),
        LongArray::class to CircularLongArraySerializer(), ULong::class to ULong.serializer(),
        ULongArray::class to CircularULongArraySerializer(), Int::class to Int.serializer(),
        IntArray::class to CircularIntArraySerializer(), UInt::class to UInt.serializer(),
        UIntArray::class to CircularUIntArraySerializer(), Short::class to Short.serializer(),
        ShortArray::class to CircularShortArraySerializer(), UShort::class to UShort.serializer(),
        UShortArray::class to CircularUShortArraySerializer(), Byte::class to Byte.serializer(),
        ByteArray::class to CircularByteArraySerializer(), UByte::class to UByte.serializer(),
        UByteArray::class to CircularUByteArraySerializer(), Boolean::class to Boolean.serializer(),
        BooleanArray::class to CircularBooleanArraySerializer(), Unit::class to Unit.serializer(),
        Nothing::class to CircularNothingSerializer(), Duration::class to Duration.serializer())

@SharedImmutable
internal val NULL = Any()

internal const val deprecationMessage =
        "This class is used only by the plugin in generated code and should not be used directly. Use corresponding factory functions instead"

