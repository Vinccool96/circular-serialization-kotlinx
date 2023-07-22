package org.cirjson.serialization.test

// from https://github.com/JetBrains/kotlin/blob/569187a7516e2e5ab217158a3170d4beb0c5cb5a/js/js.translator/testData/_commonFiles/testUtils.kt#L3
internal fun isLegacyBackend(): Boolean =
        // Using eval to prevent DCE from thinking that following code depends on Kotlin module.
        eval("(typeof Kotlin != \"undefined\" && typeof Kotlin.kotlin != \"undefined\")").unsafeCast<Boolean>()
