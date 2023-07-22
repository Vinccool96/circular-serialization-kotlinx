package org.cirjson.serialization

// Serializable data class with explicit companion object
@CircularSerializable
data class DataExplicit(val value1: String, val value2: Int) {

    companion object

}