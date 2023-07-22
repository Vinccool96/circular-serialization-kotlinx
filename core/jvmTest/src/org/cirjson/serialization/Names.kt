package org.cirjson.serialization

// Specify serializable names
@CircularSerializable
data class Names(@SerialName("value1") val custom1: String, @SerialName("value2") val custom2: Int)