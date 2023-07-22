package org.cirjson.serialization.internal

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.encoding.AbstractCircularEncoder
import org.cirjson.serialization.modules.CircularSerializersModule
import org.cirjson.serialization.modules.EmptyCircularSerializersModule

/**
 * Encoder that does not do any operations. Its main purpose is to ignore data instead of writing it.
 */
@OptIn(ExperimentalCircularSerializationApi::class)
internal object NoOpCircularEncoder : AbstractCircularEncoder() {

    override val serializersModule: CircularSerializersModule = EmptyCircularSerializersModule()

    public override fun encodeValue(value: Any): Unit = Unit

    override fun encodeNull(): Unit = Unit

    override fun encodeBoolean(value: Boolean): Unit = Unit

    override fun encodeByte(value: Byte): Unit = Unit

    override fun encodeShort(value: Short): Unit = Unit

    override fun encodeInt(value: Int): Unit = Unit

    override fun encodeLong(value: Long): Unit = Unit

    override fun encodeFloat(value: Float): Unit = Unit

    override fun encodeDouble(value: Double): Unit = Unit

    override fun encodeChar(value: Char): Unit = Unit

    override fun encodeString(value: String): Unit = Unit

    override fun encodeEnum(enumDescriptor: CircularSerialDescriptor, index: Int): Unit = Unit

}