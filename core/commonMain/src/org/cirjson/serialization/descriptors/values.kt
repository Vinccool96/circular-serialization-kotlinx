package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.PolymorphicCircularSerializer
import org.cirjson.serialization.internal.CircularSerialDescriptorForNullable
import kotlin.reflect.KClass

/**
 * Returns an iterable of all descriptor [elements][CircularSerialDescriptor.getElementDescriptor].
 */
@ExperimentalCircularSerializationApi
public val CircularSerialDescriptor.elementDescriptors: Iterable<CircularSerialDescriptor>
    get() = Iterable {
        object : Iterator<CircularSerialDescriptor> {

            private var elementsLeft = elementsCount

            override fun hasNext(): Boolean = elementsLeft > 0

            override fun next(): CircularSerialDescriptor {
                return getElementDescriptor(elementsCount - (elementsLeft--))
            }

        }
    }

/**
 * Returns an iterable of all descriptor [element names][CircularSerialDescriptor.getElementName].
 */
@ExperimentalCircularSerializationApi
public val CircularSerialDescriptor.elementNames: Iterable<String>
    get() = Iterable {
        object : Iterator<String> {
            private var elementsLeft = elementsCount
            override fun hasNext(): Boolean = elementsLeft > 0

            override fun next(): String {
                return getElementName(elementsCount - (elementsLeft--))
            }
        }
    }

/**
 * Retrieves [KClass] associated with serializer and its descriptor, if it was captured.
 *
 * For schema introspection purposes, [capturedKClass] can be used in [CircularSerializersModule] as a key
 * to retrieve registered descriptor at runtime.
 * This property is intended to be used on [SerialKind.CONTEXTUAL] and [PolymorphicKind.OPEN] kinds of descriptors,
 * where actual serializer used for a property can be determined only at runtime.
 * Serializers which represent contextual serialization and open polymorphism (namely, [CircularContextualSerializer] and
 * [PolymorphicCircularSerializer]) capture statically known KClass in a descriptor and can expose it via this property.
 *
 * This property is `null` for descriptors that are not of [SerialKind.CONTEXTUAL] or [PolymorphicKind.OPEN] kinds.
 * It _may_ be `null` for descriptors of these kinds, if captured class information is unavailable for various reasons.
 * It means that schema introspection should be performed in an application-specific manner.
 *
 * ### Example
 * Imagine we need to find all distinct properties names, which may occur in output after serializing a given class
 * with respect to [`@Contextual`][Contextual] annotation and all possible inheritors when the class is
 * serialized polymorphically.
 * Then we can write following function:
 * ```
 * fun allDistinctNames(descriptor: SerialDescriptor, module: SerialModule) = when (descriptor.kind) {
 *   is PolymorphicKind.OPEN -> module.getPolymorphicDescriptors(descriptor)
 *     .map { it.elementNames() }.flatten().toSet()
 *   is SerialKind.CONTEXTUAL -> module.getContextualDescriptor(descriptor)
 *     ?.elementNames().orEmpty().toSet()
 *   else -> descriptor.elementNames().toSet()
 * }
 * ```
 * @see CircularSerializersModule.getContextualDescriptor
 * @see CircularSerializersModule.getPolymorphicDescriptors
 */
@ExperimentalCircularSerializationApi
public val CircularSerialDescriptor.capturedKClass: KClass<*>?
    get() = when (this) {
        is CircularContextDescriptor -> kClass
        is CircularSerialDescriptorForNullable -> original.capturedKClass
        else -> null
    }

/**
 * Returns new serial descriptor for the same type with [isNullable][CircularSerialDescriptor.isNullable]
 * property set to `true`.
 */
@OptIn(ExperimentalCircularSerializationApi::class)
public val CircularSerialDescriptor.nullable: CircularSerialDescriptor
    get() {
        if (this.isNullable) return this
        return CircularSerialDescriptorForNullable(this)
    }
