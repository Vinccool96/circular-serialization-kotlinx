package org.cirjson.serialization.internal

import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

/**
 * Workaround of https://youtrack.jetbrains.com/issue/KT-54611 and https://github.com/Kotlin/kotlinx.serialization/issues/2065
 */
internal class KTypeWrapper(private val origin: KType) : KType {

    override val annotations: List<Annotation>
        get() = origin.annotations

    override val arguments: List<KTypeProjection>
        get() = origin.arguments

    override val classifier: KClassifier?
        get() = origin.classifier

    override val isMarkedNullable: Boolean
        get() = origin.isMarkedNullable

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (origin != (other as? KTypeWrapper)?.origin) return false

        val kClassifier = classifier
        if (kClassifier is KClass<*>) {
            val otherClassifier = (other as? KType)?.classifier
            if (otherClassifier == null || otherClassifier !is KClass<*>) {
                return false
            }
            return kClassifier.java == otherClassifier.java
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return origin.hashCode()
    }

    override fun toString(): String {
        return "KTypeWrapper: $origin"
    }

}