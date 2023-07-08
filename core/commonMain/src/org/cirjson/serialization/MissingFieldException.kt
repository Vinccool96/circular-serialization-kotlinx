package org.cirjson.serialization

/**
 * Thrown when [CircularKSerializer] did not receive a non-optional property from [CompositeDecoder] and [CompositeDecoder.decodeElementIndex]
 * had already returned [CompositeDecoder.DECODE_DONE].
 *
 * [MissingFieldException] is thrown on missing field from all [auto-generated][Serializable] serializers and it
 * is recommended to throw this exception from user-defined serializers.
 *
 * @see CircularSerializationException
 * @see CircularKSerializer
 *
 * @property missingFields List of fields that were required but not found during deserialization.
 * Contains at least one element.
 */
@ExperimentalCircularSerializationApi
public class MissingFieldException(public val missingFields: List<String>, message: String?, cause: Throwable?) :
        CircularSerializationException(message, cause) {

    /**
     * Creates an instance of [MissingFieldException] for the given [missingFields] and [serialName] of
     * the corresponding serializer.
     */
    public constructor(missingFields: List<String>, serialName: String) : this(missingFields,
            if (missingFields.size == 1) "Field '${missingFields[0]}' is required for type with serial name '$serialName', but it was missing"
            else "Fields $missingFields are required for type with serial name '$serialName', but they were missing",
            null)

    /**
     * Creates an instance of [MissingFieldException] for the given [missingField] and [serialName] of
     * the corresponding serializer.
     */
    public constructor(missingField: String, serialName: String) : this(listOf(missingField),
            "Field '$missingField' is required for type with serial name '$serialName', but it was missing", null)

    @PublishedApi // Constructor used by the generated serializers
    internal constructor(missingField: String) : this(listOf(missingField),
            "Field '$missingField' is required, but it was missing", null)

}