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
 * Inline instanceTag wrapper for storing injectable class
 *
 * @param instanceTag - String tag for instance `key` storage
 * @param originalTag - String for instance generic name
 */
//@JvmInline
data class KodiTagWrapper(
    private val instanceTag: KodiKeyWrapper,
    private val originalTag: KodiKeyWrapper,
) {

    /**
     * Check instanceTag is not empty
     *
     * @return [Boolean]
     */
    fun isNotEmpty(): Boolean {
        return instanceTag.isNotEmpty()
    }

    /**
     * Return a [String] representation of Wrapper
     */
    fun asString(): String = this.instanceTag.asString()

    /**
     * Return a [String] representation of Original Generic Class Name
     */
    fun asOriginal(): String = this.originalTag.asString()

    override fun toString(): String {
        return asString()
    }

    companion object {
        /**
         * Empty instance tag holder.
         * This means that [KodiHolder] instance is no in dependency graph
         */
        fun emptyTag(): KodiTagWrapper {
            return KodiTagWrapper(
                instanceTag = KodiKeyWrapper(""),
                originalTag = KodiKeyWrapper(""),
            )
        }
    }
}

/**
 * Typealias for merge tag with scope
 */
internal typealias KodiTagScopeWrappers = Pair<KodiTagWrapper, KodiKeyWrapper>

/**
 * Bind instanceTag withScope available instance holders
 *
 * @param instance - [KodiHolder] instance for store
 */
inline infix fun <reified T : KodiHolder<*>> KodiTagWrapper.with(instance: T) {
    instance tag this
}

/**
 * Merge scope tag with instance tag
 *
 * @param instance - current [KodiHolder] instance for merge and store
 */
inline infix fun <reified T : KodiHolder<*>> KodiTagScopeWrappers.with(instance: T) {
    val (kodiTagWrapper, kodiScopeWrapper) = this
    (instance at kodiScopeWrapper) tag kodiTagWrapper
}

/**
 * Add scope tag to binding instance
 *
 * @param scopeName - String scope name
 */
@CanThrowException(SCOPE_EMPTY_ERROR)
infix fun KodiTagWrapper.at(scopeName: String): KodiTagScopeWrappers {
    if (scopeName.isEmpty()) throwKodiException<IllegalStateException>(SCOPE_EMPTY_ERROR)
    return this to scopeName.asScope()
}

/**
 * Scope wrapper class
 *
 * @param key - String tag for moduleScope `key` storage
 */
@JvmInline
value class KodiKeyWrapper(private val key: String) {
    /**
     * Is Wrapper [key] empty
     *
     * @return [Boolean]
     */
    fun isNotEmpty(): Boolean = this.key.isNotEmpty()

    /**
     * Return a [String] representation of Wrapper
     */
    fun asString(): String = this.key
}