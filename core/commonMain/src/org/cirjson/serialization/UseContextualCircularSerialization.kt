package org.cirjson.serialization

import kotlin.reflect.KClass

/**
 * Instructs the plugin to use [ContextualSerializer] for every type in the current file that is listed in the [forClasses].
 *
 * @see Contextual
 * @see ContextualSerializer
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.BINARY)
public annotation class UseContextualCircularSerialization(vararg val forClasses: KClass<*>)