package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.StructureKind.*
import org.cirjson.serialization.modules.CircularSerializersModule

/**
 * Serial kind is an intrinsic property of [CircularSerialDescriptor] that indicates how
 * the corresponding type is structurally represented by its serializer.
 *
 * Kind is used by serialization formats to determine how exactly the given type
 * should be serialized. For example, JSON format detects the kind of the value and,
 * depending on that, may write it as a plain value for primitive kinds, open a
 * curly brace '{' for class-like structures and square bracket '[' for list- and array- like structures.
 *
 * Kinds are used both during serialization, to serialize a value properly and statically, and
 * to introspect the type structure or build serialization schema.
 *
 * Kind should match the structure of the serialized form, not the structure of the corresponding Kotlin class.
 * Meaning that if serializable class `class IntPair(val left: Int, val right: Int)` is represented by the serializer
 * as a single `Long` value, its descriptor should have [PrimitiveKind.LONG] without nested elements even though the class itself
 * represents a structure with two primitive fields.
 */
@ExperimentalCircularSerializationApi
public sealed class SerialKind {

    /**
     * Represents a Kotlin [Enum] with statically known values.
     * All enum values should be enumerated in descriptor elements.
     * Each element descriptor of a [Enum] kind represents an instance of a particular enum
     * and has an [StructureKind.OBJECT] kind.
     * Each [positional name][SerialDescriptor.getElementName] contains a corresponding enum element [name][Enum.name].
     *
     * Corresponding encoder and decoder methods are [Encoder.encodeEnum] and [Decoder.decodeEnum].
     */
    @ExperimentalCircularSerializationApi
    public object ENUM : SerialKind()

    /**
     * Represents an "unknown" type that will be known only at the moment of the serialization.
     * Effectively it defers the choice of the serializer to a moment of the serialization, and can
     * be used for [contextual][Contextual] serialization.
     *
     * To introspect descriptor of this kind, an instance of [CircularSerializersModule] is required.
     * See [capturedKClass] extension property for more details.
     * However, if possible options are known statically (e.g. for sealed classes), they can be
     * enumerated in child descriptors similarly to [ENUM].
     */
    @ExperimentalCircularSerializationApi
    public object CONTEXTUAL : SerialKind()

    override fun toString(): String { // KNPE should never happen, because SerialKind is sealed and all inheritors are non-anonymous
        return this::class.simpleName!!
    }

    // Provide a stable hashcode for objects
    override fun hashCode(): Int = toString().hashCode()

}
