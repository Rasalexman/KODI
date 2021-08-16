// Copyright (c) 2020 Aleksandr Minkin aka Rasalexman (sphc@yandex.ru)
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
 * Default SCOPE name for all instances that doest have a scope name in bindings
 */
internal val defaultScope = KodiScopeWrapper("DEFAULT_SCOPE")

/**
 * Empty instance tag holder.
 * This means that [KodiHolder] instance is no in dependency graph
 */
fun emptyTag() = KodiTagWrapper("", "")

/**
 * String moduleScope name to Scope Wrapper
 */
fun String?.asScope(): KodiScopeWrapper {
  return this?.takeIf { it.isNotEmpty() }?.let {
      KodiScopeWrapper(scopeTag = it)
  } ?: defaultScope
}

/**
 * Inline fun to convert any to [KodiTagWrapper]
 */
inline fun <reified T : Any> String?.asTag(): KodiTagWrapper {
    val originalTag = genericName<T>()
    val instanceTag = this?.takeIf { it.isNotEmpty() } ?: originalTag
    return KodiTagWrapper(
        instanceTag = instanceTag,
        originalTag = originalTag
    )
}

/**
 * Add Instance Tag to moduleScope
 *
 * @param scopeWrapper - [KodiScopeWrapper] to add instance tag
 */
infix fun<T : Any> KodiHolder<T>.at(scopeWrapper: KodiScopeWrapper): KodiHolder<T> {
    return this.scopeWith(scopeWrapper)
}

/**
 * Apply [IKodiModule] scope to created holder
 */
fun<T : Any> KodiHolder<T>.withModuleScope(kodiImpl: IKodi): KodiHolder<T> {
    return if(kodiImpl is IKodiModule) {
        this at kodiImpl.scope
    } else this
}

/**
 * Add [KodiTagWrapper] to current Holder
 * And put it into dependency scope
 *
 * @param instanceTag - tag for instance binding
 */
infix fun<T : Any> KodiHolder<T>.tag(instanceTag: KodiTagWrapper): KodiHolder<T> {
    return this.tagWith(instanceTag)
}

/**
 * Another elvis compare high-order function
 */
fun <T> T?.or(block: () -> T): T {
    return this ?: block()
}

/**
 * Take generic `qualifiedName` from class
 */
inline fun <reified T : Any> genericName(): String {
    return T::class.qualifiedName.toString()
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