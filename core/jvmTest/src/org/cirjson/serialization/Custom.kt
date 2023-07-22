package org.cirjson.serialization

// Custom serializer
@CircularSerializable(with = CustomSerializer::class)
data class Custom(val _value1: String, val _value2: Int)