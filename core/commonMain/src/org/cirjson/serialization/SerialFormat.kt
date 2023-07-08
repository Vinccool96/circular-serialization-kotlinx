package org.cirjson.serialization

import org.cirjson.serialization.modules.CircularSerializersModule

/**
 * Represents an instance of a serialization format
 * that can interact with [CircularKSerializer] and is a supertype of all entry points for a serialization.
 * It does not impose any restrictions on a serialized form or underlying storage, neither it exposes them.
 *
 * Concrete data types and API for user-interaction are responsibility of a concrete subclass or subinterface,
 * for example [StringFormat], [BinaryFormat] or `Json`.
 *
 * Typically, formats have their specific [Encoder] and [Decoder] implementations
 * as private classes and do not expose them.
 *
 * ### Exception types for `SerialFormat` implementation
 *
 * Methods responsible for format-specific encoding and decoding are allowed to throw
 * any subtype of [IllegalArgumentException] in order to indicate serialization
 * and deserialization errors. It is recommended to throw subtypes of [CircularSerializationException]
 * for encoder and decoder specific errors and [IllegalArgumentException] for input
 * and output validation-specific errors.
 *
 * For formats
 *
 * ### Not stable for inheritance
 *
 * `SerialFormat` interface is not stable for inheritance in 3rd party libraries, as new methods
 * might be added to this interface or contracts of the existing methods can be changed.
 *
 * It is safe to operate with instances of `SerialFormat` and call its methods.
 */
public interface SerialFormat {

    /**
     * Contains all serializers registered by format user for [Contextual] and [Polymorphic] serialization.
     *
     * The same module should be exposed in the format's [Encoder] and [Decoder].
     */
    public val serializersModule: CircularSerializersModule

}