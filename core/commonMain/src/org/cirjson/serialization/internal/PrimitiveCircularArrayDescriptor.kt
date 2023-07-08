package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

/**
 * Descriptor for primitive arrays, such as [IntArray], [DoubleArray], etc...
 *
 * Can be obtained from corresponding serializers (e.g. [ByteArraySerializer.descriptor])
 */
@OptIn(ExperimentalCircularSerializationApi::class)
internal class PrimitiveCircularArrayDescriptor internal constructor(primitive: CircularSerialDescriptor) :
    CircularListLikeDescriptor(primitive) {

    override val serialName: String = "${primitive.serialName}Array"

}