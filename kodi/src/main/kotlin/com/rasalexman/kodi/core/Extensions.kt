// Copyright (c) 2019 Aleksandr Minkin (sphc@yandex.ru)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package com.rasalexman.kodi.core

/**
 * Empty moduleScope.
 * This is means that tag has no scope
 */
fun emptyScope() = KodiScopeWrapper("")

/**
 * Default SCOPE name for all instances that doest have a scope name in bindings
 */
val defaultScope = KodiScopeWrapper("DEFAULT_SCOPE")

/**
 * Empty instance tag holder.
 * This means that [KodiHolder] instance is no in dependency graph
 */
fun emptyTag() = KodiTagWrapper("")

/**
 * String moduleScope name to Scope Wrapper
 */
fun String.asScope() = KodiScopeWrapper(this)

/**
 * String tag name to Tag Wrapper
 */
fun String.asTag() = KodiTagWrapper(this)

inline fun <reified T : Any> T.asTag() = KodiTagWrapper(T::class.java.simpleName)


/**
 * Make prediction for [KodiHolder] and do some action if prediction is not null
 *
 * @param prediction - some generic transformation as? T
 *
 * @param action - some action with conditional
 */
fun <T : IKodi> KodiHolder.holderAs(prediction: T?, action: KodiHolder.(T) -> Unit): KodiHolder {
    prediction?.let { kodiInstance ->
        this.action(kodiInstance)
    }
    return this
}

/**
 * Helper throwable method
 *
 * @param message - Message for notify user in log
 */
inline fun <reified T : Exception> throwKodiException(message: String): Nothing {
    val exception = when (T::class) {
        RuntimeException::class -> RuntimeException(message)
        ClassCastException::class -> ClassCastException(message)
        IllegalStateException::class -> IllegalStateException(message)
        IllegalArgumentException::class -> IllegalArgumentException(message)
        else -> NoSuchElementException(message)
    }
    throw exception
}