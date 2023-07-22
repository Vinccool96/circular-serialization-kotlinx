@file:OptIn(ExperimentalCircularSerializationApi::class)

package org.cirjson.serialization

import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.AbstractCircularDecoder
import org.cirjson.serialization.encoding.CircularCompositeDecoder
import org.cirjson.serialization.encoding.CircularCompositeDecoder.Companion.UNKNOWN_NAME
import org.cirjson.serialization.encoding.CircularCompositeEncoder
import org.cirjson.serialization.encoding.AbstractCircularEncoder
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.modules.EmptyCircularSerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.time.Duration

/*
 * Test ensures that type that aggregate all basic (primitive/collection/maps/arrays)
 * types is properly serialized/deserialized with dummy format that supports only classes and primitives as
 * first-class citizens.
 */
class BasicTypesCircularSerializationTest {

    @Test
    fun testKvSerialization() {
        // serialize to string
        val sb = StringBuilder()
        val out = KeyValueOutput(sb)
        out.encodeSerializableValue(TypesUmbrella.serializer(), umbrellaInstance)
        // deserialize from string
        val str = sb.toString()
        val inp = KeyValueInput(Parser(StringReader(str)))
        val other = inp.decodeSerializableValue(TypesUmbrella.serializer())
        // assert we've got it back from string
        assertEquals(umbrellaInstance, other)
        assertNotSame(umbrellaInstance, other)
    }

    @Test
    fun testEncodeDuration() {
        val sb = StringBuilder()
        val out = KeyValueOutput(sb)

        val duration = Duration.parseIsoString("P4DT12H30M5S")
        out.encodeSerializableValue(Duration.serializer(), duration)

        assertEquals("\"${duration.toIsoString()}\"", sb.toString())
    }

    @Test
    fun testDecodeDuration() {
        val durationString = "P4DT12H30M5S"
        val inp = KeyValueInput(Parser(StringReader("\"$durationString\"")))
        val other = inp.decodeSerializableValue(Duration.serializer())
        assertEquals(Duration.parseIsoString(durationString), other)
    }

    @Test
    fun testNothingSerialization() {
        // impossible to deserialize Nothing
        assertFailsWith(SerializationException::class, "'kotlin.Nothing' does not have instances") {
            val inp = KeyValueInput(Parser(StringReader("42")))
            @Suppress("IMPLICIT_NOTHING_TYPE_ARGUMENT_IN_RETURN_POSITION")
            inp.decodeSerializableValue(NothingSerializer())
        }

        // it is possible to serialize only `null` for `Nothing?`
        val sb = StringBuilder()
        val out = KeyValueOutput(sb)
        out.encodeNullableSerializableValue(NothingSerializer(), null)
        assertEquals("null", sb.toString())
    }

    // KeyValue Input/Output
    class KeyValueOutput(private val sb: StringBuilder) : AbstractCircularEncoder() {

        override val serializersModule: CircularSerializersModule = EmptyCircularSerializersModule()

        override fun beginStructure(descriptor: CircularSerialDescriptor): CircularCompositeEncoder {
            sb.append('{')
            return this
        }

        override fun endStructure(descriptor: CircularSerialDescriptor) {
            sb.append('}')
        }

        override fun encodeElement(descriptor: CircularSerialDescriptor, index: Int): Boolean {
            if (index > 0) sb.append(", ")
            sb.append(descriptor.getElementName(index))
            sb.append(':')
            return true
        }

        override fun encodeNull() {
            sb.append("null")
        }

        override fun encodeValue(value: Any) {
            sb.append(value)
        }

        override fun encodeString(value: String) {
            sb.append('"')
            sb.append(value)
            sb.append('"')
        }

        override fun encodeChar(value: Char) = encodeString(value.toString())

    }

    class KeyValueInput(private val inp: Parser) : AbstractCircularDecoder() {

        override val serializersModule: CircularSerializersModule = EmptyCircularSerializersModule()

        override fun beginStructure(descriptor: CircularSerialDescriptor): CircularCompositeDecoder {
            inp.expectAfterWhiteSpace('{')
            return this
        }

        override fun endStructure(descriptor: CircularSerialDescriptor) = inp.expectAfterWhiteSpace('}')

        override fun decodeElementIndex(descriptor: CircularSerialDescriptor): Int {
            inp.skipWhitespace(',')
            val name = inp.nextUntil(':', '}')
            if (name.isEmpty()) return CircularCompositeDecoder.DECODE_DONE
            val index = descriptor.getElementIndex(name)
            check(index != UNKNOWN_NAME)
            inp.expect(':')
            return index
        }

        private fun readToken(): String {
            inp.skipWhitespace()
            return inp.nextUntil(' ', ',', '}')
        }

        override fun decodeNotNullMark(): Boolean {
            inp.skipWhitespace()
            return inp.cur != 'n'.code
        }

        override fun decodeNull(): Nothing? {
            check(readToken() == "null") { "'null' expected" }
            return null
        }

        override fun decodeBoolean(): Boolean = readToken() == "true"

        override fun decodeByte(): Byte = readToken().toByte()

        override fun decodeShort(): Short = readToken().toShort()

        override fun decodeInt(): Int = readToken().toInt()

        override fun decodeLong(): Long = readToken().toLong()

        override fun decodeFloat(): Float = readToken().toFloat()

        override fun decodeDouble(): Double = readToken().toDouble()

        override fun decodeEnum(enumDescriptor: CircularSerialDescriptor): Int {
            return readToken().toInt()
        }

        override fun decodeString(): String {
            inp.expectAfterWhiteSpace('"')
            val value = inp.nextUntil('"')
            inp.expect('"')
            return value
        }

        override fun decodeChar(): Char = decodeString().single()

    }

    // Very simple char-by-char parser
    class Parser(private val inp: StringReader) {

        var cur: Int = inp.read()

        fun next() {
            cur = inp.read()
        }

        fun skipWhitespace(vararg c: Char) {
            while (cur >= 0 && (cur.toChar().isWhitespace() || cur.toChar() in c)) next()
        }

        fun expect(c: Char) {
            check(cur == c.code) { "Expected '$c'" }
            next()
        }

        fun expectAfterWhiteSpace(c: Char) {
            skipWhitespace()
            expect(c)
        }

        fun nextUntil(vararg c: Char): String {
            val sb = StringBuilder()
            while (cur >= 0 && cur.toChar() !in c) {
                sb.append(cur.toChar())
                next()
            }
            return sb.toString()
        }

    }

    class StringReader(val str: String) {

        private var position: Int = 0

        fun read(): Int = when (position) {
            str.length -> -1
            else -> str[position++].code
        }

    }

}