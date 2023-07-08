package org.cirjson.serialization.modules

import kotlin.js.JsName
import kotlin.native.concurrent.SharedImmutable

/**
 * A [CircularSerializersModule] which is empty and always returns `null`.
 */
@SharedImmutable
@Deprecated("Deprecated in the favour of 'EmptySerializersModule()'", level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("EmptySerializersModule()"))
@JsName("EmptySerializersModuleLegacyJs") // Compatibility with JS
public val EmptyCircularSerializersModule: CircularSerializersModule =
        CircularSerialModuleImpl(emptyMap(), emptyMap(), emptyMap(), emptyMap(), emptyMap())
