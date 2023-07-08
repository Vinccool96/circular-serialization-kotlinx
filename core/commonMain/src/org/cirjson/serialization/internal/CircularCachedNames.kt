package org.cirjson.serialization.internal

import org.cirjson.serialization.descriptors.CircularSerialDescriptor

/**
 * Internal interface used as a marker for [CircularSerialDescriptor] in order
 * to retrieve the set of all element names without allocations.
 * Used by our implementations as a performance optimization.
 * It's not an instance of [CircularSerialDescriptor] to simplify implementation via delegation
 */
internal interface CircularCachedNames {

    /**
     * A set of all names retrieved from [CircularSerialDescriptor.getElementName]
     */
    public val serialNames: Set<String>

}