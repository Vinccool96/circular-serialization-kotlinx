package org.cirjson.serialization

/**
 * Thrown when [CircularKSerializer] received unknown property index from [CompositeDecoder.decodeElementIndex].
 *
 * This exception means that data schema has changed in backwards-incompatible way.
 */
@PublishedApi
internal class UnknownFieldException // This constructor is used by coroutines exception recovery
internal constructor(message: String?) : CircularSerializationException(message) {

    // This constructor is used by the generated serializers
    constructor(index: Int) : this("An unknown field for index $index")

}