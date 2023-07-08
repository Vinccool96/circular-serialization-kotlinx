package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularKSerializer
import org.cirjson.serialization.CircularSerializationException
import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.InternalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.StructureKind
import org.cirjson.serialization.descriptors.buildSerialDescriptor
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularDecoder
import org.cirjson.serialization.encoding.CircularEncoder
import org.cirjson.serialization.encoding.decodeStructure

/**
 * Serializer for Kotlin's singletons (denoted by `object` keyword).
 * To preserve singleton identity after serialization and deserialization, object serializer
 * uses an [object instance][objectInstance].
 * By default, a singleton is serialized as an empty structure, e.g. `{}` in JSON.
 */
@PublishedApi
@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularObjectSerializer<T : Any>(serialName: String, private val objectInstance: T) :
    CircularKSerializer<T> {

    @PublishedApi // See comment in SealedClassSerializer
    internal constructor(serialName: String, objectInstance: T, classAnnotations: Array<Annotation>) : this(serialName,
            objectInstance) {
        _annotations = classAnnotations.asList()
    }

    private var _annotations: List<Annotation> = emptyList()

    @OptIn(InternalCircularSerializationApi::class)
    override val descriptor: CircularSerialDescriptor by lazy(LazyThreadSafetyMode.PUBLICATION) {
        buildSerialDescriptor(serialName, StructureKind.OBJECT) {
            annotations = _annotations
        }
    }

    override fun serialize(encoder: CircularEncoder, value: T) {
        encoder.beginStructure(descriptor).endStructure(descriptor)
    }

    override fun deserialize(decoder: CircularDecoder): T {
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) return@decodeStructure

            when (val index = decodeElementIndex(descriptor)) {
                CircularCompositeDecoder.DECODE_DONE -> {
                    return@decodeStructure
                }
                else -> throw CircularSerializationException("Unexpected index $index")
            }
        }
        return objectInstance
    }

}