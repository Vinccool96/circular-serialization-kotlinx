package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularHashSetClassDesc(elementDesc: CircularSerialDescriptor) :
    CircularListLikeDescriptor(elementDesc) {

    override val serialName: String get() = HASH_SET_NAME

}