// Copyright (c) 2021 Aleksandr Minkin aka Rasalexman (sphc@yandex.ru)
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

import com.rasalexman.kodi.delegates.immutableGetter

/**
 * Lambda wrapper withScope generic return Type
 */
internal typealias LambdaWithReturn<T> = () -> T

/**
 * Main storage abstraction
 */
internal interface IKodiStorage<V> {

    /**
     * Is there any instance by given key in instanceMap storage
     *
     * @param tag
     * The instance key to retrieve
     */
    fun hasInstance(tag: KodiTagWrapper): Boolean

    /**
     * Remove current instance from storage by given key
     *
     * @param tag - [KodiTagWrapper] key to remove value if it's exist
     * @param scope - [KodiScopeWrapper] String representing the moduleScope
     */
    fun removeInstance(tag: KodiTagWrapper, scope: KodiScopeWrapper): Boolean

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

    /**
     * Create or get [KodiHolder] instance from storage
     *
     * @param tag [KodiTagWrapper] - for retrieve instance by tag
     * @param scope [KodiScopeWrapper] - current scope or [defaultScope]
     * @param defaultValue [LambdaWithReturn] - lambda for create default value if it's not exist
     */
    fun createOrGet(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        defaultValue: LambdaWithReturn<V>
    ): V


}

/**
 * Main instance and scopes class
 */
abstract class KodiStorage : IKodiStorage<KodiHolder<out Any>> {

    /**
     * All instances holders storage
     */
    private val instancesStore by immutableGetter { mutableMapOf<String, KodiHolder<out Any>>() }

    /**
     *  Kodi modules set
     */
    private val modulesSet by immutableGetter { mutableSetOf<IKodiModule>() }

    /**
     * Kodi scopes sets
     */
    private val scopes by immutableGetter { mutableMapOf<String, MutableSet<String>>() }

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
            val moduleScope = module.scope
            val moduleInstanceSet = module.moduleInstancesSet.toSet()
            moduleInstanceSet.forEach { tag ->
                removeInstance(tag, moduleScope)
            }
            module.moduleInstancesSet.clear()
        }
    }

    /**
     * Remove [KodiTagWrapper] from module set
     *
     * @param - [KodiTagWrapper] to remove from [IKodiModule] moduleInstancesSet
     *
     * @return [Boolean]
     */
    override fun removeFromModule(instanceTag: KodiTagWrapper): Boolean {
        return if (instanceTag.isNotEmpty()) {
            val localModuleSet = modulesSet.toSet()
            localModuleSet.find { it.moduleInstancesSet.remove(instanceTag) } != null
        } else {
            false
        }
    }

    /**
     * Check if given [KodiTagWrapper] has been added to any [IKodiModule]
     *
     * @param - [KodiTagWrapper] to check
     *
     * @return [Boolean] - does module set has instance with given [KodiTagWrapper]
     */
    override fun hasModuleByTag(instanceTag: KodiTagWrapper): Boolean {
        return if (instanceTag.isNotEmpty()) {
            val localModuleSet = modulesSet.toSet()
            localModuleSet.firstOrNull { it.moduleInstancesSet.contains(instanceTag) } != null
        } else {
            false
        }
    }

    /**
     * Create or get value from instance map
     *
     * @param tag
     * [KodiTagWrapper] for retrieve value from map
     *
     * @param scope
     * [KodiScopeWrapper] - scope wrapper for instances
     *
     * @param defaultValue
     * [LambdaWithReturn] `fun` to create value instance for saving in storage
     *
     * @return [KodiHolder] instance
     */
    override fun createOrGet(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        defaultValue: LambdaWithReturn<KodiHolder<out Any>>
    ): KodiHolder<out Any> {
        val key = createKey(tag, scope)
        return instancesStore.getOrPut(key, defaultValue)
    }

    /**
     * Is there any instance by given key in instanceMap storage
     *
     * @param tag
     * The instance [KodiTagWrapper] to retrieve
     *
     * @return [Boolean] - does store has instance with tag [KodiTagWrapper]
     */
    override fun hasInstance(tag: KodiTagWrapper): Boolean {
        return takeFilteredKeys(tag.asString()).isNotEmpty()
    }

    /**
     * Remove current instance from storage by given key
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     *
     * @return - optional [KodiHolder]
     */
    override fun removeInstance(tag: KodiTagWrapper, scope: KodiScopeWrapper): Boolean {
        return tag.takeIf { it.isNotEmpty() }?.let {
            // if we has scope to delete instance, create key and remove current instance
            val key = createKey(tag, scope, false)

            val isRemoved = instancesStore.remove(key)?.apply {
                clear()
            } != null

            if (isRemoved) {
                val originalTag = tag.asOriginal()
                val localScopes = scopes[originalTag]
                localScopes?.apply {
                    remove(key)
                    if (isEmpty()) {
                        scopes.remove(originalTag)
                    }
                }
            }
            isRemoved
        }.or { false }
    }

    /**
     * Remove moduleScope and all it's instances from storage
     *
     * @param scope - moduleScope tag for remove
     *
     * @return [Boolean] - is references deleted
     */
    override fun removeAllScope(scope: KodiScopeWrapper): Boolean {
        val scopedList = takeFilteredKeys(scope.toString())
        scopedList.forEach {
            instancesStore.remove(it)?.clear()
        }
        return scopedList.isNotEmpty()
    }

    /**
     * Remove all instances and scopes from dependency graph
     * Warning!!! - this action cannot be reverted
     */
    override fun clearAll() {
        instancesStore.clear()
        modulesSet.clear()
        scopes.clear()
    }

    /**
     * Filter keys by some params
     * @param filter - current filter text for keys
     *
     * @return [List] of store keys
     */
    private fun takeFilteredKeys(filter: String): List<String> {
        val localInstanceStore = instancesStore.toMap()
        return localInstanceStore.filterKeys { it.contains(filter, true) }.keys.toList()
    }


    /**
     * Create key for all data instances
     *
     * @param tag - [KodiTagWrapper]
     * @param scope - [KodiScopeWrapper]
     * @param withCopy - [Boolean] does we need to add copied instance to graph
     *
     * @return [String] of generated storage key
     */
    protected open fun createKey(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        withCopy: Boolean = true
    ): String {
        val instanceKey = "${scope.asString()}_${tag.asString()}"
        // find in local copy
        val localStore = instancesStore.toMap()
        // if it does not exist add to scope set
        return if (!localStore.containsKey(instanceKey)) {
            val originalTag = tag.asOriginal()
            // if we don't need to add copied we find key or add copied holder to key
            if (withCopy) {
                localStore.addKeyIfNotExist(instanceKey, originalTag)
            } else {
                localStore.takeKeyFromScope(instanceKey, originalTag)
            }
        } else {
            instanceKey
        }
    }

    /**
     * Add Key if it doesn't added to graph
     */
    private fun Map<String, KodiHolder<out Any>>.addKeyIfNotExist(
        instanceKey: String,
        originalTag: String
    ): String {
        val tagScopes = scopes.getOrPut(originalTag) { mutableSetOf() }
        if (tagScopes.isEmpty()) {
            tagScopes.add(instanceKey)
        } else {
            tagScopes.findHolder(this)?.let { existedHolder ->
                tagScopes.add(instanceKey)
                instancesStore[instanceKey] = existedHolder
            }.or {
                tagScopes.add(instanceKey)
            }
        }
        return instanceKey
    }

    /**
     * Find [KodiHolder] and take key from it
     */
    private fun Map<String, KodiHolder<out Any>>.takeKeyFromScope(
        instanceKey: String,
        originalTag: String
    ): String {
        val existedHolder = scopes[originalTag]?.findHolder(this)
        return existedHolder?.storageKey ?: instanceKey
    }

    /**
     * find [KodiHolder] for instance key search
     */
    private fun Set<String>.findHolder(instanceMap: Map<String, KodiHolder<out Any>>): KodiHolder<out Any>? {
        if (isEmpty()) {
            return null
        }
        val localScopes = this.toList()
        return localScopes.firstNotNullOfOrNull {
            instanceMap[it]
        }
    }
}