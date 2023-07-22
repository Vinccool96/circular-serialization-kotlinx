package org.cirjson.serialization.test

actual val currentPlatform: Platform = if (isLegacyBackend()) Platform.JS_LEGACY else Platform.JS_IR
