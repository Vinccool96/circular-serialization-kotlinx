package org.cirjson.serialization.descriptors

import org.cirjson.serialization.ExperimentalCircularSerializationApi

@OptIn(ExperimentalCircularSerializationApi::class)
internal class WrappedCircularSerialDescriptor(override val serialName: String, original: CircularSerialDescriptor) :
    CircularSerialDescriptor by original