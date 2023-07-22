package org.cirjson.serialization.test

import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
actual val currentPlatform: Platform = Platform.NATIVE
