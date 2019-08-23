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
 * Inline instanceTag wrapper for storing injectable class
 *
 * @param instanceTag - String tag for instance `key` storage
 */
inline class KodiTagWrapper(private val instanceTag: String) : IKodi {
    /**
     * Bind instanceTag with available instance holders
     */
    infix fun with(instance: KodiHolder): KodiTagWrapper {
        Kodi.createOrGet(this) { instance }
        return this
    }

    /**
     * Add Instance instanceTag to scope
     */
    infix fun at(scopeWrapper: KodiScopeWrapper): KodiTagWrapper {
        Kodi.addToScope(scopeWrapper to this)
        return this
    }
}

/**
 * Scoper wrapper class
 *
 * @param scopeTag - String tag for scope `key` storage
 */
inline class KodiScopeWrapper(private val scopeTag: String)