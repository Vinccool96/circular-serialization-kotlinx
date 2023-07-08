package org.cirjson.serialization.internal

import org.cirjson.serialization.CircularSerializationException
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

@JvmName("throwSubtypeNotRegistered")
internal fun throwSubtypeNotRegistered(subClassName: String?, baseClass: KClass<*>): Nothing {
    val scope = "in the scope of '${baseClass.simpleName}'"
    throw CircularSerializationException(
            if (subClassName == null)
                "Class discriminator was missing and no default polymorphic serializers were registered $scope"
            else
                "Class '$subClassName' is not registered for polymorphic serialization $scope.\n" +
                        "To be registered automatically, class '$subClassName' has to be '@Serializable', and the base class '${baseClass.simpleName}' has to be sealed and '@Serializable'.\n" +
                        "Alternatively, register the serializer for '$subClassName' explicitly in a corresponding SerializersModule."
    )
}

@JvmName("throwSubtypeNotRegistered")
internal fun throwSubtypeNotRegistered(subClass: KClass<*>, baseClass: KClass<*>): Nothing =
        throwSubtypeNotRegistered(subClass.simpleName ?: "$subClass", baseClass)
