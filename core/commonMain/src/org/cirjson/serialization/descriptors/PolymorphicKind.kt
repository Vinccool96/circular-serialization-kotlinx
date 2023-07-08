package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.PolymorphicCircularSerializer
import org.cirjson.serialization.SealedClassCircularSerializer
import org.cirjson.serialization.modules.CircularSerializersModule

/**
 * Polymorphic kind represents a (bounded) polymorphic value, that is referred
 * by some base class or interface, but its structure is defined by one of the possible implementations.
 * Polymorphic kind is, by its definition, a union kind and is extracted to its own subtype to emphasize
 * bounded and sealed polymorphism common property: not knowing the actual type statically and requiring
 * formats to additionally encode it.
 */
@ExperimentalCircularSerializationApi
public sealed class PolymorphicKind : SerialKind() {

    /**
     * Sealed kind represents Kotlin sealed classes, where all subclasses are known statically at the moment of declaration.
     * [SealedClassCircularSerializer] can be used as an example of sealed serialization.
     */
    public object SEALED : PolymorphicKind()

    /**
     * Open polymorphic kind represents statically unknown type that is hidden behind a given base class or interface.
     * [PolymorphicCircularSerializer] can be used as an example of polymorphic serialization.
     *
     * Due to security concerns and typical mistakes that arises from polymorphic serialization, by default
     * `kotlinx.serialization` provides only bounded polymorphic serialization, forcing users to register all possible
     * serializers for a given base class or interface.
     *
     * To introspect descriptor of this kind (e.g. list possible subclasses), an instance of [CircularSerializersModule] is required.
     * See [capturedKClass] extension property for more details.
     */
    public object OPEN : PolymorphicKind()

}