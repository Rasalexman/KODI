// Copyright (c) 2018 Aleksandr Minkin (sphc@yandex.ru)
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

package com.mincor.kodi.ext

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * Provider holder data class is a function holder with params for future or immediately call
 *
 * @param function
 * Function to call later with params
 *
 * @param params
 * Required params for function call
 */
data class ProviderHolder<T>(var function: KFunction<T?>? = null, var params: MutableMap<KParameter, Any?>? = null) {
    /**
     * Call the function with given params
     *
     * @param newParams
     * new function parameters to invoke
     */
    fun call(newParams: List<Any?>? = null): T? {
        params = newParams?.toValMap(function?.parameters) ?: params ?: mutableMapOf()
        return function?.callBy(params!!)!!
    }

    /**
     * Clear all references from object
     */
    fun clear() {
        function = null
        params = null
    }
}