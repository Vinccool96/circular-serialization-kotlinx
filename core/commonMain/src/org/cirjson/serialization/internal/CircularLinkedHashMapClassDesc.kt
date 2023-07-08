package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularLinkedHashMapClassDesc(keyDesc: CircularSerialDescriptor, valueDesc: CircularSerialDescriptor) :
    CircularMapLikeDescriptor(LINKED_HASH_MAP_NAME, keyDesc, valueDesc)