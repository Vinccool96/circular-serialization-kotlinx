package org.cirjson.serialization

/**
 * A generic exception indicating the problem in serialization or deserialization process.
 *
 * This is a generic exception type that can be thrown during problems at any stage of the serialization,
 * including encoding, decoding, serialization, deserialization, and validation.
 * [SerialFormat] implementors should throw subclasses of this exception at any unexpected event,
 * whether it is a malformed input or unsupported class layout.
 *
 * [CircularSerializationException] is a subclass of [IllegalArgumentException] for the sake of consistency and user-defined validation:
 * Any serialization exception is triggered by the illegal input, whether
 * it is a serializer that does not support specific structure or an invalid input.
 *
 * It is also an established pattern to validate input in user's classes in the following manner:
 * ```
 * @CircularSerializable
 * class Foo(...) {
 *     init {
 *         required(age > 0) { ... }
 *         require(name.isNotBlank()) { ... }
 *     }
 * }
 * ```
 * While clearly being serialization error (when compromised data was deserialized),
 * Kotlin way is to throw `IllegalArgumentException` here instead of using library-specific `SerializationException`.
 *
 * For general "catch-all" patterns around deserialization of potentially
 * untrusted/invalid/corrupted data it is recommended to catch `IllegalArgumentException` type
 * to avoid catching irrelevant to serialization errors such as `OutOfMemoryError` or domain-specific ones.
 */
public open class CircularSerializationException : IllegalArgumentException {

    /**
     * Creates an instance of [CircularSerializationException] without any details.
     */
    public constructor()

    /**
     * Creates an instance of [CircularSerializationException] with the specified detail [message].
     */
    public constructor(message: String?) : super(message)

    /**
     * Creates an instance of [CircularSerializationException] with the specified detail [message], and the given [cause].
     */
    public constructor(message: String?, cause: Throwable?) : super(message, cause)

    /**
     * Creates an instance of [CircularSerializationException] with the specified [cause].
     */
    public constructor(cause: Throwable?) : super(cause)

}

