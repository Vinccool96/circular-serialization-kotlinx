package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor

@OptIn(ExperimentalCircularSerializationApi::class)
internal class CircularHashMapClassDesc(keyDesc: CircularSerialDescriptor, valueDesc: CircularSerialDescriptor) :
    CircularMapLikeDescriptor(HASH_MAP_NAME, keyDesc, valueDesc)
