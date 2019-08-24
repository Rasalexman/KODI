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

package com.mincor.kodi.core

/**
 * Scope name wrapper
 */
fun IKodi.scope(scopeInitTag: () -> String) = KodiScopeWrapper(scopeInitTag())

/**
 * Empty moduleScope.
 * This is means that tag has no scope
 */
fun emptyScope() = KodiScopeWrapper("")

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

/**
 * Apply actions to <T> if prediction is true
 *
 * @param prediction true prediction
 * @param action action to do
 */
fun <T> T.applyIf(prediction: Boolean, action: (T)->Unit): T {
    if(prediction) action(this)
    return this
}
