package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularArrayListClassDesc(elementDesc: CircularSerialDescriptor) :
    CircularListLikeDescriptor(elementDesc) {

    override val serialName: String get() = ARRAY_LIST_NAME

}