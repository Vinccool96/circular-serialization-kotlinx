package org.cirjson.serialization.internal

import org.cirjson.serialization.InternalCircularSerializationApi

@Suppress("Unused")
@PublishedApi
@OptIn(InternalCircularSerializationApi::class)
internal class InlineClassDescriptor(name: String, generatedSerializer: GeneratedCircularSerializer<*>) :
    PluginGeneratedSerialDescriptor(name, generatedSerializer, 1) {

    override val isInline: Boolean = true

    override fun hashCode(): Int = super.hashCode() * 31

    override fun equals(other: Any?): Boolean = equalsImpl(other) { otherDescriptor ->
        otherDescriptor.isInline && typeParameterDescriptors.contentEquals(otherDescriptor.typeParameterDescriptors)
    }

}