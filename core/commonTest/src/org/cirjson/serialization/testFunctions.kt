//@file:OptIn(ExperimentalCircularSerializationApi::class)

package org.cirjson.serialization

import org.cirjson.serialization.descriptors.CircularSerialDescriptor

fun getSerialId(desc: CircularSerialDescriptor, index: Int): Int?
        = desc.findAnnotation<Id>(index)?.id

@OptIn(ExperimentalCircularSerializationApi::class)
inline fun <reified A: Annotation> CircularSerialDescriptor.findAnnotation(elementIndex: Int): A? {
    val candidates = getElementAnnotations(elementIndex).filterIsInstance<A>()
    return when (candidates.size) {
        0 -> null
        1 -> candidates[0]
        else -> throw IllegalStateException("There are duplicate annotations of type ${A::class} in the descriptor $this")
    }
}
