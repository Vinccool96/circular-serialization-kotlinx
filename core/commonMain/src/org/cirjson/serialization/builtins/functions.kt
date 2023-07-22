@file:Suppress("FunctionName") @file:OptIn(InternalCircularSerializationApi::class)

package org.cirjson.serialization.builtins

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.PrimitiveKind
import org.cirjson.serialization.descriptors.StructureKind
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.internal.*
import kotlin.reflect.KClass
import kotlin.time.Duration

/**
 * Returns built-in serializer for Kotlin [Pair].
 * Resulting serializer represents pair as a structure of two key-value pairs.
 */
public fun <K, V> CircularPairSerializer(keySerializer: CircularKSerializer<K>,
        valueSerializer: CircularKSerializer<V>): CircularKSerializer<Pair<K, V>> =
        org.cirjson.serialization.internal.CircularPairSerializer(keySerializer, valueSerializer)

/**
 * Returns built-in serializer for [Map.Entry].
 * Resulting serializer represents entry as a structure with a single key-value pair.
 * E.g. `Pair(1, 2)` and `Map.Entry(1, 2)` will be serialized to JSON as
 * `{"first": 1, "second": 2}` and {"1": 2} respectively.
 */
public fun <K, V> CircularMapEntrySerializer(keySerializer: CircularKSerializer<K>,
        valueSerializer: CircularKSerializer<V>): CircularKSerializer<Map.Entry<K, V>> =
        org.cirjson.serialization.internal.CircularMapEntrySerializer(keySerializer, valueSerializer)

/**
 * Returns built-in serializer for Kotlin [Triple].
 * Resulting serializer represents triple as a structure of three key-value pairs.
 */
public fun <A, B, C> CircularTripleSerializer(aSerializer: CircularKSerializer<A>, bSerializer: CircularKSerializer<B>,
        cSerializer: CircularKSerializer<C>): CircularKSerializer<Triple<A, B, C>> =
        org.cirjson.serialization.internal.CircularTripleSerializer(aSerializer, bSerializer, cSerializer)

/**
 * Returns serializer for [Char] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.CHAR] kind.
 */
public fun Char.Companion.serializer(): CircularKSerializer<Char> = CharSerializer

/**
 * Returns serializer for [CharArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [Char.Companion.serializer].
 */
@Suppress("UNCHECKED_CAST")
public fun CircularCharArraySerializer(): CircularKSerializer<CharArray> = CircularCharArraySerializer

/**
 * Returns serializer for [Byte] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.BYTE] kind.
 */
public fun Byte.Companion.serializer(): CircularKSerializer<Byte> = ByteSerializer

/**
 * Returns serializer for [ByteArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [Byte.Companion.serializer].
 */
public fun CircularByteArraySerializer(): CircularKSerializer<ByteArray> = CircularByteArraySerializer

/**
 * Returns serializer for [UByteArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [UByte.Companion.serializer].
 */
@ExperimentalCircularSerializationApi
@ExperimentalUnsignedTypes
public fun CircularUByteArraySerializer(): CircularKSerializer<UByteArray> = CircularUByteArraySerializer

/**
 * Returns serializer for [Short] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.SHORT] kind.
 */
public fun Short.Companion.serializer(): CircularKSerializer<Short> = ShortSerializer

/**
 * Returns serializer for [ShortArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [Short.Companion.serializer].
 */
public fun CircularShortArraySerializer(): CircularKSerializer<ShortArray> = CircularShortArraySerializer

/**
 * Returns serializer for [UShortArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [UShort.Companion.serializer].
 */
@ExperimentalCircularSerializationApi
@ExperimentalUnsignedTypes
public fun CircularUShortArraySerializer(): CircularKSerializer<UShortArray> = CircularUShortArraySerializer

/**
 * Returns serializer for [Int] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.INT] kind.
 */
public fun Int.Companion.serializer(): CircularKSerializer<Int> = IntSerializer

/**
 * Returns serializer for [IntArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [Int.Companion.serializer].
 */
public fun CircularIntArraySerializer(): CircularKSerializer<IntArray> = CircularIntArraySerializer

/**
 * Returns serializer for [UIntArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [UInt.Companion.serializer].
 */
@ExperimentalCircularSerializationApi
@ExperimentalUnsignedTypes
public fun CircularUIntArraySerializer(): CircularKSerializer<UIntArray> = CircularUIntArraySerializer

/**
 * Returns serializer for [Long] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.LONG] kind.
 */
public fun Long.Companion.serializer(): CircularKSerializer<Long> = LongSerializer

/**
 * Returns serializer for [LongArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [Long.Companion.serializer].
 */
public fun CircularLongArraySerializer(): CircularKSerializer<LongArray> = CircularLongArraySerializer

/**
 * Returns serializer for [ULongArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [ULong.Companion.serializer].
 */
@ExperimentalCircularSerializationApi
@ExperimentalUnsignedTypes
public fun CircularULongArraySerializer(): CircularKSerializer<ULongArray> = CircularULongArraySerializer

/**
 * Returns serializer for [Float] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.FLOAT] kind.
 */
public fun Float.Companion.serializer(): CircularKSerializer<Float> = FloatSerializer

/**
 * Returns serializer for [FloatArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [Float.Companion.serializer].
 */
public fun CircularFloatArraySerializer(): CircularKSerializer<FloatArray> = CircularFloatArraySerializer

/**
 * Returns serializer for [Double] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.DOUBLE] kind.
 */
public fun Double.Companion.serializer(): CircularKSerializer<Double> = DoubleSerializer

/**
 * Returns serializer for [DoubleArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [Double.Companion.serializer].
 */
public fun CircularDoubleArraySerializer(): CircularKSerializer<DoubleArray> = CircularDoubleArraySerializer

/**
 * Returns serializer for [Boolean] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.BOOLEAN] kind.
 */
public fun Boolean.Companion.serializer(): CircularKSerializer<Boolean> = BooleanSerializer

/**
 * Returns serializer for [BooleanArray] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized one by one with [Boolean.Companion.serializer].
 */
public fun CircularBooleanArraySerializer(): CircularKSerializer<BooleanArray> = CircularBooleanArraySerializer

/**
 * Returns serializer for [Unit] with [descriptor][CircularSerialDescriptor] of [StructureKind.OBJECT] kind.
 */
@Suppress("unused")
public fun Unit.serializer(): CircularKSerializer<Unit> = UnitSerializer

/**
 * Returns serializer for [String] with [descriptor][CircularSerialDescriptor] of [PrimitiveKind.STRING] kind.
 */
public fun String.Companion.serializer(): CircularKSerializer<String> = StringSerializer

/**
 * Returns serializer for reference [Array] of type [E] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized with the given [elementSerializer].
 */
@Suppress("UNCHECKED_CAST")
@ExperimentalCircularSerializationApi
public inline fun <reified T : Any, reified E : T?> CircularArraySerializer(
        elementSerializer: CircularKSerializer<E>): CircularKSerializer<Array<E>> =
        CircularArraySerializer<T, E>(T::class, elementSerializer)

/**
 * Returns serializer for reference [Array] of type [E] with [descriptor][CircularSerialDescriptor] of [StructureKind.LIST] kind.
 * Each element of the array is serialized with the given [elementSerializer].
 */
@ExperimentalCircularSerializationApi
public fun <T : Any, E : T?> CircularArraySerializer(kClass: KClass<T>,
        elementSerializer: CircularKSerializer<E>): CircularKSerializer<Array<E>> =
        CircularReferenceArraySerializer<T, E>(kClass, elementSerializer)

/**
 * Creates a serializer for [`List<T>`][List] for the given serializer of type [T].
 */
public fun <T> CircularListSerializer(elementSerializer: CircularKSerializer<T>): CircularKSerializer<List<T>> =
        CircularArrayListSerializer(elementSerializer)

/**
 * Creates a serializer for [`Set<T>`][Set] for the given serializer of type [T].
 */
public fun <T> CircularSetSerializer(elementSerializer: CircularKSerializer<T>): CircularKSerializer<Set<T>> =
        CircularLinkedHashSetSerializer(elementSerializer)

/**
 * Creates a serializer for [`Map<K, V>`][Map] for the given serializers for
 * its ket type [K] and value type [V].
 */
public fun <K, V> CircularMapSerializer(keySerializer: CircularKSerializer<K>,
        valueSerializer: CircularKSerializer<V>): CircularKSerializer<Map<K, V>> =
        CircularLinkedHashMapSerializer(keySerializer, valueSerializer)

/**
 * Returns serializer for [UInt].
 */
public fun UInt.Companion.serializer(): CircularKSerializer<UInt> = UIntSerializer

/**
 * Returns serializer for [ULong].
 */
public fun ULong.Companion.serializer(): CircularKSerializer<ULong> = ULongSerializer

/**
 * Returns serializer for [UByte].
 */
public fun UByte.Companion.serializer(): CircularKSerializer<UByte> = UByteSerializer

/**
 * Returns serializer for [UShort].
 */
public fun UShort.Companion.serializer(): CircularKSerializer<UShort> = UShortSerializer

/**
 * Returns serializer for [Duration].
 * It is serialized as a string that represents a duration in the ISO-8601-2 format.
 *
 * The result of serialization is similar to calling [Duration.toIsoString], for deserialization is [Duration.parseIsoString].
 */
public fun Duration.Companion.serializer(): CircularKSerializer<Duration> = CircularDurationSerializer

/**
 * Returns serializer for [Nothing].
 * Throws an exception when trying to encode or decode.
 *
 * It is used as a dummy in case it is necessary to pass a type to a parameterized class. At the same time, it is expected that this generic type will not participate in serialization.
 */
@ExperimentalCircularSerializationApi
public fun CircularNothingSerializer(): CircularKSerializer<Nothing> = CircularNothingSerializer
