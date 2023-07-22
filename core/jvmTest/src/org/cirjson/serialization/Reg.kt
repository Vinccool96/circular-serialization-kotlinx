package org.cirjson.serialization

// Regular (non-data) class with var properties
@CircularSerializable
class Reg {

    var value1: String = ""

    var value2: Int = 0

}