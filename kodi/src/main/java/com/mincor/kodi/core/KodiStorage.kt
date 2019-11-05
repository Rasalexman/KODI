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
 * Lambda wrapper withScope generic return Type
 */
typealias LambdaWithReturn<T> = () -> T

/**
 * Main storage abstraction
 */
interface IKodiStorage<V> {
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
    fun removeInstance(key: KodiTagWrapper): KodiHolder?

    /**
     * Add Our dependency tag to moduleScope storage
     *
     * @param item Pair<KodiScopeWrapper, KodiTagWrapper>
     */
    fun addToScope(item: ScopeItem)

    /**
     * Remove moduleScope item
     *
     * @param item - moduleScope item to Remove
     */
    fun removeFromScope(item: ScopeItem): Boolean

    /**
     * Remove moduleScope by it tag wrapper
     *
     * @param scope String representing the moduleScope
     */
    fun removeAllScope(scope: KodiScopeWrapper): Boolean


    fun addModule(module: IKodiModule)

    fun removeModule(module: IKodiModule)

    /**
     * Remove all instances and scopes from dependency graph
     * Warning!!! - this action cannot be reverted
     */
    fun clearAll()
}

/**
 * Main instance and scopes class
 */
abstract class KodiStorage : IKodiStorage<KodiHolder> {

    /**
     * Main instance storage (`MutableMap<[KodiTagWrapper], T>`)
     */
    private val instanceMap by immutableGetter { mutableMapOf<KodiTagWrapper, KodiHolder>() }

    /**
     * Main `moduleScope` to `tags` storage instance (`MutableSet<[ScopeSetItem]>`)
     */
    private val scopeSet by immutableGetter { mutableSetOf<ScopeSetItem>() }

    /**
     *  Kodi modules set
     */
    private val modulesSet by immutableGetter { mutableSetOf<IKodiModule>() }

    /**
     *
     */
    override fun addModule(module: IKodiModule) {
        val initializer = module.instanceInitializer
        module.initializer()
        modulesSet.add(module)
    }

    /**
     *
     */
    override fun removeModule(module: IKodiModule) {
        with(module) {
            if(modulesSet.remove(this)) {
                moduleHolders.forEach { tag ->
                    removeInstance(tag.asTag())
                }
                moduleHolders.clear()
            }
        }

    }

    /**
     * Create or get value from instance map
     *
     * @param key
     * [KodiTagWrapper] for retrieve value from map
     *
     * @param defaultValue
     * [LambdaWithReturn] `fun` to create value instance for saving in storage
     */
    override fun createOrGet(key: KodiTagWrapper, defaultValue: LambdaWithReturn<KodiHolder>): KodiHolder {
        return instanceMap.getOrPut(key, defaultValue)
    }

    /**
     * Is there any instance by given key in instanceMap storage
     *
     * @param key
     * The instance [KodiTagWrapper] to retrieve
     */
    override fun hasInstance(key: KodiTagWrapper): Boolean {
        return instanceMap.containsKey(key)
    }

    /**
     * Remove current instance from storage by given key
     *
     * @param key - [KodiTagWrapper] to remove value if it's exist
     */
    override fun removeInstance(key: KodiTagWrapper): KodiHolder? {
        return if(key.isNotEmpty()) instanceMap.remove(key) else null
    }

    /**
     * Add Our dependency tag to moduleScope storage
     *
     * @param item - Pair<[KodiScopeWrapper], [KodiTagWrapper]> to be added
     */
    override fun addToScope(item: ScopeItem) {
        val (scope, tag) = item
        scopeSet.apply {
            findTypeSet(scope)?.add(tag)
                    ?: add(scope to mutableSetOf(tag))
        }
    }

    /**
     * Remove instance tag from given moduleScope
     *
     * @param item - [ScopeItem] to remove tag
     */
    override fun removeFromScope(item: ScopeItem): Boolean {
        return item.let { (scope, tag) ->
            scopeSet.findTypeSet(scope)?.remove(tag) ?: false
        }
    }

    /**
     * Remove moduleScope and all it's instances from storage
     *
     * @param scope - moduleScope tag for remove
     */
    override fun removeAllScope(scope: KodiScopeWrapper): Boolean {
        return scopeSet.findTypeSet(scope)
                ?.forEach { typeWrapper -> removeInstance(typeWrapper) }
                .runCatching { scopeSet.removeAll { it.first == scope } }
                .isSuccess
    }

    /**
     * Remove all instances and scopes from dependency graph
     * Warning!!! - this action cannot be reverted
     */
    override fun clearAll() {
        scopeSet.clear()
        instanceMap.clear()
    }

    /**
     * Find an [TypesSet] by given `moduleScope: [KodiScopeWrapper]
     *
     * @param scope - moduleScope tag wrapper for search
     * @receiver `MutableSet` of [ScopeSetItem]
     */
    private fun MutableSet<ScopeSetItem>.findTypeSet(scope: KodiScopeWrapper): TypesSet? {
        return this.find { it.first == scope }?.second
    }
}