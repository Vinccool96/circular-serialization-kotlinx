package org.cirjson.serialization

/**
 * [SerialFormat] that allows conversions to and from [String] via [encodeToString] and [decodeFromString] methods.
 *
 * ### Not stable for inheritance
 *
 * `StringFormat` interface is not stable for inheritance in 3rd party libraries, as new methods
 * might be added to this interface or contracts of the existing methods can be changed.
 *
 * It is safe to operate with instances of `StringFormat` and call its methods.
 */
public interface StringFormat : SerialFormat {

    /**
     * Serializes and encodes the given [value] to string using the given [serializer].
     *
     * @throws CircularSerializationException in case of any encoding-specific error
     * @throws IllegalArgumentException if the encoded input does not comply format's specification
     */
    public fun <T> encodeToString(serializer: CircularSerializationStrategy<T>, value: T): String

    /**
     * Decodes and deserializes the given [string] to the value of type [T] using the given [deserializer].
     *
     * @throws CircularSerializationException in case of any decoding-specific error
     * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
     */
    public fun <T> decodeFromString(deserializer: CircularDeserializationStrategy<T>, string: String): T

}