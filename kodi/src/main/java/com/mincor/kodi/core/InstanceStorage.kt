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

import com.mincor.kodi.delegates.immutableGetter

/**
 * ScopeItem simple wrapper Pair<KodiScopeWrapper, KodiTagWrapper> "scopeName" to "tagName"
 * for input value in `fun addToScope`
 */
typealias ScopeItem = Pair<KodiScopeWrapper, KodiTagWrapper>

/**
 * TypesSet wrapper for KodiTagWrapper
 */
typealias TypesSet = MutableSet<KodiTagWrapper>

/**
 * ScopeSetItem wrapper for Scope storage
 */
typealias ScopeSetItem = Pair<KodiScopeWrapper, TypesSet>

/**
 * Lambda wrapper with generic return Type
 */
typealias LambdaWithReturn<T> = () -> T

/**
 * Main storage abstraction
 */
interface IInstanceStorage<V> {
    /**
     * Create or get value from instance map
     *
     * @param key
     * Key for retrieve value from map
     *
     * @param defaultValue
     * Lambda `fun` to create value instance for saving in storage
     */
    fun createOrGet(key: KodiTagWrapper, defaultValue: LambdaWithReturn<V>): V

    /**
     * Is there any instance by given key in instanceMap storage
     *
     * @param key
     * The instance key to retrieve
     */
    fun hasInstance(key: KodiTagWrapper): Boolean

    /**
     * Remove current instance from storage by given key
     *
     * @param key - key to remove value if it's exist
     */
    fun removeInstance(key: KodiTagWrapper): Boolean

    /**
     * Add Our dependency tag to scope storage
     *
     * @param item Pair<KodiScopeWrapper, KodiTagWrapper>
     */
    fun addToScope(item: ScopeItem)
}

/**
 * Main instance and scopes class
 */
abstract class InstanceStorage<T : Any> : IInstanceStorage<T> {
    /**
     * Main instance storage MutableMap
     */
    private val instanceMap by immutableGetter { mutableMapOf<KodiTagWrapper, T>() }

    /**
     * Main scope to tags storage
     */
    private val scopeSet by immutableGetter { mutableSetOf<ScopeSetItem>() }

    /**
     * Create or get value from instance map
     *
     * @param key
     * Key for retrieve value from map
     *
     * @param defaultValue
     * Lambda `fun` to create value instance for saving in storage
     */
    override fun createOrGet(key: KodiTagWrapper, defaultValue: LambdaWithReturn<T>): T {
        return instanceMap.getOrPut(key, defaultValue)
    }

    /**
     * Is there any instance by given key in instanceMap storage
     *
     * @param key
     * The instance key to retrieve
     */
    override fun hasInstance(key: KodiTagWrapper): Boolean {
        return instanceMap.containsKey(key)
    }

    /**
     * Remove current instance from storage by given key
     *
     * @param key - key to remove value if it's exist
     */
    override fun removeInstance(key: KodiTagWrapper): Boolean {
        return instanceMap.remove(key) != null
    }


    /**
     * Add Our dependency tag to scope storage
     *
     * @param item Pair<KodiScopeWrapper, KodiTagWrapper>
     */
    override fun addToScope(item: ScopeItem) {
        val (scope, tag) = item
        scopeSet.find { it.first == scope }?.second?.apply {
            if (!contains(tag)) add(tag)
        } ?: scopeSet.apply {
            add(scope to mutableSetOf(tag))
        }
    }
}