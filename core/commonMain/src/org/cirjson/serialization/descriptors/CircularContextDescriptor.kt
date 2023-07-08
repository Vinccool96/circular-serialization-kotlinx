package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.modules.CircularSerializersModule
import kotlin.jvm.JvmField
import kotlin.reflect.KClass

/**
 * Descriptor that captures [kClass] and allows retrieving additional runtime information,
 * if proper [CircularSerializersModule] is provided.
 */
@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularContextDescriptor(
        private val original: CircularSerialDescriptor,
        @JvmField val kClass: KClass<*>
) : CircularSerialDescriptor by original {

    override val serialName = "${original.serialName}<${kClass.simpleName}>"

    override fun equals(other: Any?): Boolean {
        val another = other as? CircularContextDescriptor ?: return false
        return original == another.original && another.kClass == this.kClass
    }

    override fun hashCode(): Int {
        var result = kClass.hashCode()
        result = 31 * result + serialName.hashCode()
        return result
    }

    override fun toString(): String {
        return "ContextCircularDescriptor(kClass: $kClass, original: $original)"
    }

}