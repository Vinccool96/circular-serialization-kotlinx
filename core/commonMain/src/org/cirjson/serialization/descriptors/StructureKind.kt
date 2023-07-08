package org.cirjson.serialization.descriptors

import org.cirjson.serialization.CircularSerializable
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.StructureKind.*

/**
 * Structure kind represents values with composite structure of nested elements of depth and arbitrary number.
 * We acknowledge following structured kinds:
 *
 * ### Regular classes
 * The most common case for serialization, that represents an arbitrary structure with fixed count of elements.
 * When the regular Kotlin class is marked as [CircularSerializable], its descriptor kind will be [CLASS].
 *
 * ### Lists
 * [LIST] represent a structure with potentially unknown in advance number of elements of the same type.
 * All standard serializable [List] implementors and arrays are represented as [LIST] kind of the same type.
 *
 * ### Maps
 * [MAP] represent a structure with potentially unknown in advance number of key-value pairs of the same type.
 * All standard serializable [Map] implementors are represented as [Map] kind of the same type.
 *
 * ### Kotlin objects
 * A singleton object defined with `object` keyword with an [OBJECT] kind.
 * By default, objects are serialized as empty structures without any states and their identity is preserved
 * across serialization within the same process, so you always have the same instance of the object.
 *
 * ### Serializers interaction
 * Serialization formats typically handle these kinds by marking structure start and end.
 * E.g. the following serializable class `class IntHolder(myValue: Int)` of structure kind [CLASS] is handled by
 * serializer as the following call sequence:
 * ```
 * val composite = encoder.beginStructure(descriptor) // Denotes the start of the structure
 * composite.encodeIntElement(descriptor, index = 0, holder.myValue)
 * composite.endStructure(descriptor) // Denotes the end of the structure
 * ```
 * and its corresponding [Decoder] counterpart.
 *
 * ### Serial descriptor implementors note
 * These kinds can be used not only for collection and regular classes.
 * For example, provided serializer for [Map.Entry] represents it as [Map] type, so it is serialized
 * as `{"actualKey": "actualValue"}` map directly instead of `{"key": "actualKey", "value": "actualValue"}`
 */
@ExperimentalCircularSerializationApi
public sealed class StructureKind : SerialKind() {

    /**
     * Structure kind for regular classes with an arbitrary, but known statically, structure.
     * Serializers typically encode classes with calls to [Encoder.beginStructure] and [CompositeEncoder.endStructure],
     * writing the elements of the class between these calls.
     */
    public object CLASS : StructureKind()

    /**
     * Structure kind for lists and arrays of an arbitrary length.
     * Serializers typically encode classes with calls to [Encoder.beginCollection] and [CompositeEncoder.endStructure],
     * writing the elements of the list between these calls.
     * Built-in list serializers treat elements as homogeneous, though application-specific serializers may impose
     * application-specific restrictions on specific [LIST] types.
     *
     * Example of such application-specific serialization may be class `class ListOfThreeElements() : List<Any>`,
     * for which an author of the serializer knows that while it is `List<Any>`, in fact, is always has three elements
     * of a known type (e.g. the first is always a string, the second one is always an int etc.)
     */
    public object LIST : StructureKind()

    /**
     * Structure kind for maps of an arbitrary length.
     * Serializers typically encode classes with calls to [Encoder.beginCollection] and [CompositeEncoder.endStructure],
     * writing the elements of the map between these calls.
     *
     * Built-in map serializers treat elements as homogeneous, though application-specific serializers may impose
     * application-specific restrictions on specific [MAP] types.
     */
    public object MAP : StructureKind()

    /**
     * Structure kind for singleton objects defined with `object` keyword.
     * By default, objects are serialized as empty structures without any state and their identity is preserved
     * across serialization within the same process, so you always have the same instance of the object.
     *
     * Empty structure is represented as a call to [Encoder.beginStructure] with the following [CompositeEncoder.endStructure]
     * without any intermediate encodings.
     */
    public object OBJECT : StructureKind()

}