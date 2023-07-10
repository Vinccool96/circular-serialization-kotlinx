package org.cirjson.serialization.internal

/**
 * Suppresses Animal Sniffer plugin errors for certain classes.
 * Such classes are not available in Android API, but used only for JVM.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
internal annotation class SuppressAnimalSniffer
