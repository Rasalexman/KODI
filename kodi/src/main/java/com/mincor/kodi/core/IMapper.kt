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

package com.mincor.kodi.core

import com.mincor.kodi.ext.ProviderHolder

/**
 * Helper interface for bind generic instances with name
 */
interface IMapper<T : Any> {
    /**
     * Main entities(instances) holder map
     */
    val instanceMap: MutableMap<String, T>
}

/**
 * Create or get value from instance map
 *
 * @param key
 * Key for retrieve value from map
 *
 * @param inst
 * lambda func to create value instance for save
 */
inline fun <reified T : Any> IMapper<T>.createOrGet(key: String, inst: () -> T): T = this.instanceMap.getOrPut(key) { inst() }

/**
 * Is there any instance by given key
 *
 * @param key
 * The instance key to retrieve
 */
inline fun <reified T : Any> IMapper<T>.hasInstance(key: String): Boolean = this.instanceMap.containsKey(key)

/**
 *  Remove all instances from map
 */
inline fun <reified T : Any> IMapper<T>.removeAll() {
    instanceMap.forEach { (_, instance) ->
        (instance as? ProviderHolder<T?>)?.clear()
    }
    instanceMap.clear()
}

/**
 * Remove instance from map by given key
 *
 * @param key
 * Key to remove instance
 */
inline fun <reified T : Any> IMapper<T>.removeInstance(key: String) {
    if (hasInstance(key)) (this.instanceMap.remove(key) as? ProviderHolder<T?>)?.clear()
}
