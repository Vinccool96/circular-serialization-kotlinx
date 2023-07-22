package org.cirjson.serialization

import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.descriptors.StructureKind
import org.cirjson.serialization.encoding.AbstractCircularDecoder
import org.cirjson.serialization.encoding.AbstractCircularEncoder
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularCompositeEncoder
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.modules.EmptyCircularSerializersModule
import org.junit.Test

class SerializeFlatTest {

    @Test
    fun testData() {
        val out = Out("Data")
        out.encodeSerializableValue(serializer(), Data("s1", 42))
        out.done()

        val inp = Inp("Data")
        val data = inp.decodeSerializableValue(serializer<Data>())
        inp.done()
        assert(data.value1 == "s1" && data.value2 == 42)
    }

    @Test
    fun testDataExplicit() {
        val out = Out("DataExplicit")
        out.encodeSerializableValue(serializer(), DataExplicit("s1", 42))
        out.done()

        val inp = Inp("DataExplicit")
        val data = inp.decodeSerializableValue(serializer<DataExplicit>())
        inp.done()
        assert(data.value1 == "s1" && data.value2 == 42)
    }

    @Test
    fun testReg() {
        val out = Out("Reg")
        val reg = Reg()
        reg.value1 = "s1"
        reg.value2 = 42
        out.encodeSerializableValue(serializer(), reg)
        out.done()

        val inp = Inp("Reg")
        val data = inp.decodeSerializableValue(serializer<Reg>())
        inp.done()
        assert(data.value1 == "s1" && data.value2 == 42)
    }

    @Test
    fun testNames() {
        val out = Out("Names")
        out.encodeSerializableValue(serializer(), Names("s1", 42))
        out.done()

        val inp = Inp("Names")
        val data = inp.decodeSerializableValue(serializer<Names>())
        inp.done()
        assert(data.custom1 == "s1" && data.custom2 == 42)
    }

    @Test
    fun testCustom() {
        val out = Out("Custom")
        out.encodeSerializableValue(CustomSerializer, Custom("s1", 42))
        out.done()

        val inp = Inp("Custom")
        val data = inp.decodeSerializableValue(CustomSerializer)
        inp.done()
        assert(data._value1 == "s1" && data._value2 == 42)
    }

    @Test
    fun testExternalData() {
//        val out = Out("ExternalData")
//        out.encodeSerializableValue(ExternalSerializer, ExternalData("s1", 42))
//        out.done()
//
//        val inp = Inp("ExternalData")
//        val data = inp.decodeSerializableValue(ExternalSerializer)
//        inp.done()
//        assert(data.value1 == "s1" && data.value2 == 42)
    }

    companion object {

        fun fail(msg: String): Nothing = throw RuntimeException(msg)

        fun checkDesc(name: String, desc: CircularSerialDescriptor) {
            if (desc.serialName != "org.cirjson.serialization.$name") fail("checkDesc name $desc")
            if (desc.kind != StructureKind.CLASS) fail("checkDesc kind ${desc.kind}")
            if (desc.getElementName(0) != "value1") fail("checkDesc[0] $desc")
            if (desc.getElementName(1) != "value2") fail("checkDesc[1] $desc")
        }

    }

    class Out(private val name: String) : AbstractCircularEncoder() {

        var step = 0

        override val serializersModule: CircularSerializersModule = EmptyCircularSerializersModule()

        override fun beginStructure(descriptor: CircularSerialDescriptor): CircularCompositeEncoder {
            checkDesc(name, descriptor)
            if (step == 0) step++ else fail("@$step: beginStructure($descriptor)")
            return this
        }

        override fun encodeElement(descriptor: CircularSerialDescriptor, index: Int): Boolean {
            checkDesc(name, descriptor)
            when (step) {
                1 -> if (index == 0) {
                    step++
                    return true
                }
                3 -> if (index == 1) {
                    step++
                    return true
                }
            }
            fail("@$step: encodeElement($descriptor, $index)")
        }

        override fun encodeString(value: String) {
            when (step) {
                2 -> if (value == "s1") {
                    step++
                    return
                }
            }
            fail("@$step: encodeString($value)")
        }

        override fun encodeInt(value: Int) {
            when (step) {
                4 -> if (value == 42) {
                    step++
                    return
                }
            }
            fail("@$step: decodeInt($value)")
        }

        override fun endStructure(descriptor: CircularSerialDescriptor) {
            checkDesc(name, descriptor)
            if (step == 5) step++ else fail("@$step: endStructure($descriptor)")
        }

        fun done() {
            if (step != 6) fail("@$step: OUT FAIL")
        }

    }

    class Inp(private val name: String) : AbstractCircularDecoder() {

        var step = 0

        override val serializersModule: CircularSerializersModule = EmptyCircularSerializersModule()

        override fun beginStructure(descriptor: CircularSerialDescriptor): CircularCompositeDecoder {
            checkDesc(name, descriptor)
            if (step == 0) step++ else fail("@$step: beginStructure($descriptor)")
            return this
        }

        override fun decodeElementIndex(descriptor: CircularSerialDescriptor): Int {
            checkDesc(name, descriptor)
            when (step) {
                1 -> {
                    step++
                    return 0
                }
                3 -> {
                    step++
                    return 1
                }
                5 -> {
                    step++
                    return -1
                }
            }
            fail("@$step: decodeElementIndex($descriptor)")
        }

        override fun decodeString(): String {
            when (step) {
                2 -> {
                    step++
                    return "s1"
                }
            }
            fail("@$step: decodeString()")
        }

        override fun decodeInt(): Int {
            when (step) {
                4 -> {
                    step++
                    return 42
                }
            }
            fail("@$step: decodeInt()")
        }

        override fun endStructure(descriptor: CircularSerialDescriptor) {
            checkDesc(name, descriptor)
            if (step == 6) step++ else fail("@$step: endStructure($descriptor)")
        }

        fun done() {
            if (step != 7) fail("@$step: INP FAIL")
        }

    }

}