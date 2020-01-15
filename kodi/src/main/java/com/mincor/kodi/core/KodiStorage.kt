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

typealias KodiInstanceMap = MutableMap<KodiTagWrapper, KodiHolder>

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

    /**
     * Add [IKodiModule] to Kodi Module Storage set
     * Also it bind all dependencies into Kodi graph
     *
     * @param module - [IKodiModule] instance to remove
     */
    fun addModule(module: IKodiModule)

    /**
     * Remove module from Kodi Module Storage set
     * Also it remove all instances of this module from dependency graph
     *
     * @param module - [IKodiModule] instance to remove
     */
    fun removeModule(module: IKodiModule)

    /**
     * Remove [KodiTagWrapper] from module set
     *
     * @param - [KodiTagWrapper] to remove from [IKodiModule] moduleInstancesSet
     * @return [Boolean]
     */
    fun removeFromModule(instanceTag: KodiTagWrapper): Boolean

    /**
     * Check if given [KodiTagWrapper] has been added to any [IKodiModule]
     *
     * @param - [KodiTagWrapper] to check
     * @return [Boolean]
     */
    fun hasModuleByTag(instanceTag: KodiTagWrapper): Boolean

    /**
     * Remove all instances and scopes from dependency graph
     * Warning!!! - this action cannot be reverted
     */
    fun clearAll()

    fun createOrGet(key: KodiTagWrapper, scope: KodiScopeWrapper, defaultValue: LambdaWithReturn<KodiHolder>): KodiHolder
}

/**
 * Main instance and scopes class
 */
abstract class KodiStorage : IKodiStorage<KodiHolder> {

    /**
     * Main instance storage (`MutableMap<[KodiTagWrapper], T>`)
     */
    private val instanceMap by immutableGetter { mutableMapOf<KodiTagWrapper, KodiHolder>() }

    private val scopedInstanceSet by immutableGetter { mutableMapOf<KodiScopeWrapper, KodiInstanceMap>() }

    /**
     * Main `moduleScope` to `tags` storage instance (`MutableSet<[ScopeSetItem]>`)
     */
    private val scopeSet by immutableGetter { mutableSetOf<ScopeSetItem>() }

    /**
     *  Kodi modules set
     */
    private val modulesSet by immutableGetter { mutableSetOf<IKodiModule>() }

    /**
     * Add [IKodiModule] to Kodi Module Storage set
     * Also it bind all dependencies into Kodi graph
     *
     * @param module - [IKodiModule] instance to remove
     */
    override fun addModule(module: IKodiModule) {
        val initializer = module.instanceInitializer
        module.initializer()
        modulesSet.add(module)
    }

    /**
     * Remove module from Kodi Module Storage set
     * Also it remove all instances of this module from dependency graph
     *
     * @param module - [IKodiModule] instance to remove
     */
    override fun removeModule(module: IKodiModule) {
        if (modulesSet.remove(module)) {
            module.apply {
                moduleInstancesSet.apply {
                    asSequence().forEach { tag ->
                        removeInstance(tag)?.at(emptyScope())
                    }
                    clear()
                }
            }
        }
    }

    /**
     * Remove [KodiTagWrapper] from module set
     *
     * @param - [KodiTagWrapper] to remove from [IKodiModule] moduleInstancesSet
     * @return [Boolean]
     */
    override fun removeFromModule(instanceTag: KodiTagWrapper): Boolean {
        return if (instanceTag.isNotEmpty()) {
            modulesSet.find { it.moduleInstancesSet.remove(instanceTag) } != null
        } else {
            false
        }
    }

    /**
     * Check if given [KodiTagWrapper] has been added to any [IKodiModule]
     *
     * @param - [KodiTagWrapper] to check
     * @return [Boolean]
     */
    override fun hasModuleByTag(instanceTag: KodiTagWrapper): Boolean {
        return if (instanceTag.isNotEmpty()) {
            modulesSet.find { it.moduleInstancesSet.contains(instanceTag) } != null
        } else {
            false
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

    override fun createOrGet(key: KodiTagWrapper, scope: KodiScopeWrapper, defaultValue: LambdaWithReturn<KodiHolder>): KodiHolder {
        val instanceMapper = scopedInstanceSet.getOrPut(scope) { mutableMapOf() }
        return instanceMapper.getOrPut(key, defaultValue)
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
     *
     * @return - optional [KodiHolder]
     */
    override fun removeInstance(key: KodiTagWrapper): KodiHolder? {
        removeFromModule(key)
        return instanceMap.remove(key)
    }

    /**
     * Add Our dependency tag to moduleScope storage
     *
     * @param item - Pair<[KodiScopeWrapper], [KodiTagWrapper]> to be added
     */
    override fun addToScope(item: ScopeItem) {
        val (scope, tag) = item
        scopeSet.apply {
            findTagSet(scope)?.add(tag)
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
            scopeSet.findTagSet(scope)?.remove(tag) ?: false
        }
    }

    /**
     * Remove moduleScope and all it's instances from storage
     *
     * @param scope - moduleScope tag for remove
     */
    override fun removeAllScope(scope: KodiScopeWrapper): Boolean {
        return if (scope.isNotEmpty()) {
            scopeSet.findTagSet(scope)
                    ?.forEach { typeWrapper -> removeInstance(typeWrapper) }
                    .runCatching {
                        scopeSet.removeAll { it.first == scope }
                    }.isSuccess
        } else {
            false
        }
    }

    /**
     * Remove all instances and scopes from dependency graph
     * Warning!!! - this action cannot be reverted
     */
    override fun clearAll() {
        scopeSet.clear()
        instanceMap.clear()
        modulesSet.clear()
    }

    /**
     * Find an [TypesSet] by given `moduleScope: [KodiScopeWrapper]
     *
     * @param scope - moduleScope tag wrapper for search
     * @receiver `MutableSet` of [ScopeSetItem]
     */
    private fun MutableSet<ScopeSetItem>.findTagSet(scope: KodiScopeWrapper): TypesSet? {
        return scope.takeIf { it.isNotEmpty() }?.let {
            this.find { it.first == scope }?.second
        }
    }
}