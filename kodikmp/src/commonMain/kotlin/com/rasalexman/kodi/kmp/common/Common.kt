package com.rasalexman.kodi.kmp.common

import com.rasalexman.kodi.kmp.wrapper.KodiKeyWrapper

/**
 * Default SCOPE name for all instances that doest have a scope name in bindings
 */
internal val defaultScope = KodiKeyWrapper("DEFAULT_SCOPE")


/**
 * Annotation for mark some throwable functions
 *
 * @param message - string for user log output
 */
@Target(AnnotationTarget.FUNCTION)
annotation class CanThrowException(
    val message: String = "Check that the 'tag' is added to the dependency graph, otherwise it will fall with RuntimeException"
)

internal const val TAG_EMPTY_ERROR = "Parameter 'tag' cannot be empty string"
internal const val SCOPE_EMPTY_ERROR = "Parameter 'scopeName' cannot be empty string"
internal const val HOLDER_NULL_ERROR = "There is no 'KodiHolder' instance in dependency graph"
internal const val INITIALIZER_NULL_ERROR = "There is no typed initializer passed throw an exception"