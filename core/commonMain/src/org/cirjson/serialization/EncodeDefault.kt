package org.cirjson.serialization

/**
 * Controls whether the target property is serialized when its value is equal to a default value,
 * regardless of the format settings.
 * Does not affect decoding and deserialization process.
 *
 * Example of usage:
 * ```
 * @Serializable
 * data class Foo(
 *     @EncodeDefault(ALWAYS) val a: Int = 42,
 *     @EncodeDefault(NEVER) val b: Int = 43,
 *     val c: Int = 44
 * )
 *
 * Json { encodeDefaults = false }.encodeToString((Foo()) // {"a": 42}
 * Json { encodeDefaults = true }.encodeToString((Foo())  // {"a": 42, "c":44}
 * ```
 *
 * @see EncodeDefault.Mode.ALWAYS
 * @see EncodeDefault.Mode.NEVER
 */
@Target(AnnotationTarget.PROPERTY)
@ExperimentalCircularSerializationApi
public annotation class EncodeDefault(val mode: Mode = Mode.ALWAYS) {

    /**
     * Strategy for the [EncodeDefault] annotation.
     */
    @ExperimentalCircularSerializationApi
    public enum class Mode {
        /**
         * Configures serializer to always encode the property, even if its value is equal to its default.
         * For annotated properties, format settings are not taken into account and
         * [CompositeEncoder.shouldEncodeElementDefault] is not invoked.
         */
        ALWAYS,

        /**
         * Configures serializer not to encode the property if its value is equal to its default.
         * For annotated properties, format settings are not taken into account and
         * [CompositeEncoder.shouldEncodeElementDefault] is not invoked.
         */
        NEVER
    }

}