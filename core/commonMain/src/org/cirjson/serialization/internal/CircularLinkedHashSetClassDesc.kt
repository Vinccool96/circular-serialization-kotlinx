package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularLinkedHashSetClassDesc(elementDesc: CircularSerialDescriptor) :
    CircularListLikeDescriptor(elementDesc) {

    override val serialName: String get() = LINKED_HASH_SET_NAME

}