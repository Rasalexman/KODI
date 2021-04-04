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

import com.rasalexman.kodi.delegates.immutableGetter

/**
 * Lambda wrapper withScope generic return Type
 */
internal typealias LambdaWithReturn<T> = () -> T

/**
 * Instance Listener function
 */
internal typealias InstanceHandler<T> = (T) -> Unit

/**
 *
 */
internal typealias AnyInstanceHandler = InstanceHandler<Any>

/**
 *
 */
internal typealias Handlers = MutableList<AnyInstanceHandler>

/**
 * Main storage abstraction
 */
interface IKodiStorage<V> {

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
     * @param tag - key to remove value if it's exist
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
    fun createOrGet(tag: KodiTagWrapper, scope: KodiScopeWrapper, defaultValue: LambdaWithReturn<V>): V

    fun hasBindingListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper): Boolean
    fun<T> addBindingListener(tag: KodiTagWrapper, scope: KodiScopeWrapper, listener: InstanceHandler<T>)
    fun<T> removeInstanceBindedListener(tag: KodiTagWrapper, scope: KodiScopeWrapper, listener: InstanceHandler<T>): Boolean
    fun removeAllInstanceListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper)
    fun <T> notifyInstanceWasBinded(tag: KodiTagWrapper, scope: KodiScopeWrapper, instance: T)
}

/**
 * Main instance and scopes class
 */
abstract class KodiStorage : IKodiStorage<KodiHolder> {

    private val instancesStore by immutableGetter { mutableMapOf<String, KodiHolder>() }

    /**
     *  Kodi modules set
     */
    private val modulesSet by immutableGetter { mutableSetOf<IKodiModule>() }

    /**
     *
     */
    private val instanceBindedListener by immutableGetter { mutableMapOf<String, Handlers>() }
    //private val instanceUnBindedListener by immutableGetter { mutableMapOf<Pair<KodiTagWrapper, KodiScopeWrapper>, Handlers>() }

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
            val moduleInstanceSet = module.moduleInstancesSet
            moduleInstanceSet.forEach { tag ->
                removeInstance(tag, moduleScope)
            }
            moduleInstanceSet.clear()
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
     * @return [Boolean] - does module set has instance with given [KodiTagWrapper]
     */
    override fun hasModuleByTag(instanceTag: KodiTagWrapper): Boolean {
        return if (instanceTag.isNotEmpty()) {
            modulesSet.firstOrNull { it.moduleInstancesSet.contains(instanceTag) } != null
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
     */
    override fun createOrGet(tag: KodiTagWrapper, scope: KodiScopeWrapper, defaultValue: LambdaWithReturn<KodiHolder>): KodiHolder {
        val key = createKey(tag, scope)
        return instancesStore.getOrPut(key, defaultValue)
    }

    /**
     * Is there any instance by given key in instanceMap storage
     *
     * @param tag
     * The instance [KodiTagWrapper] to retrieve
     */
    override fun hasInstance(tag: KodiTagWrapper): Boolean {
        return instancesStore.filterKeys { it.contains(tag.asString()) }.isNotEmpty()
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
        return tag.takeIf {
            it.isNotEmpty()
        }?.let {
            // if we has scope to delete instance, create key and remove current instance
            if(scope.isNotEmpty()) {
                val key = createKey(tag, scope)
                instancesStore.remove(key) != null
            } else {
                val allInstances = instancesStore.filterKeys { it.contains(tag.asString()) }.keys.toList()
                allInstances.forEach {
                    instancesStore.remove(it)
                }
                allInstances.isNotEmpty()
            }
        } ?: false


        /*return tag.takeIf {
            it.isNotEmpty()
        }?.let {
            scope.takeIf { it != defaultScope }?.let {
                scopedInstanceSet[defaultScope]
                        ?.get(tag)
                        ?.scope
                        ?.takeIf { it == scope }
                        ?.let {
                            scopedInstanceSet[defaultScope]
                                    ?.remove(tag)
                                    ?.apply { clear() }
                        }
                scopedInstanceSet[scope]
                        ?.remove(tag)
                        ?.apply { clear() }
            }.or {
                scopedInstanceSet[defaultScope]
                        ?.remove(tag)
                        ?.scope
                        ?.takeIf { it != defaultScope }
                        ?.let {
                            scopedInstanceSet[it]
                                    ?.remove(tag)
                                    ?.apply { clear() }
                        }
            }
        }*/
    }

    /**
     * Remove moduleScope and all it's instances from storage
     *
     * @param scope - moduleScope tag for remove
     *
     * @return [Boolean] - is references deleted
     */
    override fun removeAllScope(scope: KodiScopeWrapper): Boolean {
        val scopedList = instancesStore.filterKeys { it.contains(scope.toString()) }.keys.toList()
        scopedList.forEach {
            instancesStore.remove(it)
        }
        return scopedList.isNotEmpty()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> addBindingListener(tag: KodiTagWrapper, scope: KodiScopeWrapper, listener: InstanceHandler<T>) {
        val key = createKey(tag, scope)
        val listeners = instanceBindedListener.getOrPut(key) { mutableListOf() }
        val anyLocalListener = listener as AnyInstanceHandler
        if(listeners.indexOf(listener) < 0) {
            listeners.add(anyLocalListener)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> removeInstanceBindedListener(tag: KodiTagWrapper, scope: KodiScopeWrapper, listener: InstanceHandler<T>): Boolean {
        val key = createKey(tag, scope)
        val listeners = instanceBindedListener[key]
        return listeners?.remove(listener as AnyInstanceHandler) ?: false
    }

    override fun removeAllInstanceListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper) {
        val key = createKey(tag, scope)
        val listeners = instanceBindedListener[key]
        listeners?.clear()
    }

    override fun hasBindingListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper): Boolean {
        val key = createKey(tag, scope)
        val listeners = instanceBindedListener[key]
        return listeners?.isNotEmpty() ?: false
    }

    @Suppress("UNCHECKED_CAST")
    override fun<T> notifyInstanceWasBinded(tag: KodiTagWrapper, scope: KodiScopeWrapper, instance: T) {
        val key = createKey(tag, scope)
        val listeners = instanceBindedListener[key]
        val localListeners = listeners.orEmpty()
        localListeners.forEach {
            (it as? InstanceHandler<T>)?.invoke(instance)
        }
    }

    /**
     * Remove all instances and scopes from dependency graph
     * Warning!!! - this action cannot be reverted
     */
    override fun clearAll() {
        instancesStore.clear()
        modulesSet.clear()
        instanceBindedListener.clear()
    }

    private fun createKey(tag: KodiTagWrapper, scope: KodiScopeWrapper): String {
        val currentScope = scope.takeIf { it != defaultScope }.or { defaultScope  }
        return "${currentScope.asString()}_${tag.asString()}"
    }
}