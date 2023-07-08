package org.cirjson.serialization

/**
 * [SerialFormat] that allows conversions to and from [ByteArray] via [encodeToByteArray] and [decodeFromByteArray] methods.
 *
 * ### Not stable for inheritance
 *
 * `BinaryFormat` interface is not stable for inheritance in 3rd party libraries, as new methods
 * might be added to this interface or contracts of the existing methods can be changed.
 *
 * It is safe to operate with instances of `BinaryFormat` and call its methods.
 */
public interface BinaryFormat : SerialFormat {

    /**
     * Serializes and encodes the given [value] to byte array using the given [serializer].
     *
     * @throws CircularSerializationException in case of any encoding-specific error
     * @throws IllegalArgumentException if the encoded input does not comply format's specification
     */
    public fun <T> encodeToByteArray(serializer: CircularSerializationStrategy<T>, value: T): ByteArray

    /**
     * Decodes and deserializes the given [byte array][bytes] to the value of type [T] using the given [deserializer].
     *
     * @throws CircularSerializationException in case of any decoding-specific error
     * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
     */
    public fun <T> decodeFromByteArray(deserializer: CircularDeserializationStrategy<T>, bytes: ByteArray): T

}