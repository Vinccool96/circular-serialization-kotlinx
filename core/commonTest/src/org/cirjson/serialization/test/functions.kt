@file:OptIn(ExperimentalCircularSerializationApi::class)

package org.cirjson.serialization.test

import org.cirjson.serialization.ExperimentalCircularSerializationApi
import org.cirjson.serialization.descriptors.CircularSerialDescriptor
import org.cirjson.serialization.internal.CircularEnumSerializer
import kotlin.test.assertEquals

fun isJs(): Boolean = currentPlatform == Platform.JS_LEGACY || currentPlatform == Platform.JS_IR

fun isJsLegacy(): Boolean = currentPlatform == Platform.JS_LEGACY

fun isJsIr(): Boolean = currentPlatform == Platform.JS_IR

fun isJvm(): Boolean = currentPlatform == Platform.JVM

fun isNative(): Boolean = currentPlatform == Platform.NATIVE

@Suppress("TestFunctionName")
internal inline fun <reified E : Enum<E>> CircularEnumSerializer(serialName: String): CircularEnumSerializer<E> =
        CircularEnumSerializer(serialName, enumValues())

fun CircularSerialDescriptor.assertDescriptorEqualsTo(other: CircularSerialDescriptor) {
    assertEquals(serialName, other.serialName)
    assertEquals(elementsCount, other.elementsCount)
    assertEquals(isNullable, other.isNullable)
    assertEquals(annotations, other.annotations)
    assertEquals(kind, other.kind)
    for (i in 0 until elementsCount) {
        getElementDescriptor(i).assertDescriptorEqualsTo(other.getElementDescriptor(i))
        val name = getElementName(i)
        val otherName = other.getElementName(i)
        assertEquals(name, otherName)
        assertEquals(getElementAnnotations(i), other.getElementAnnotations(i))
        assertEquals(name, otherName)
        assertEquals(isElementOptional(i), other.isElementOptional(i))
    }
}

inline fun noJs(test: () -> Unit) {
    if (!isJs()) test()
}

inline fun noJsLegacy(test: () -> Unit) {
    if (!isJsLegacy()) test()
}

inline fun jvmOnly(test: () -> Unit) {
    if (isJvm()) test()
}
