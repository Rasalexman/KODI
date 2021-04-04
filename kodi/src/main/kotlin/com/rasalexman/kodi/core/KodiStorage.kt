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
 *
 */
internal typealias ListenersMap = MutableMap<String, Handlers>

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

    /**
     * Add binding listeners to the list
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param listener - [InstanceHandler] function for handle binding event
     *
     */
    fun<T> addListener(tag: KodiTagWrapper, scope: KodiScopeWrapper, listener: InstanceHandler<T>, event: BindingEvent = BindingEvent.BIND)

    /**
     * Check if instance has binding listeners
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     *
     */
    fun hasListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper, event: BindingEvent = BindingEvent.BIND): Boolean

    /**
     * Remove instance from binding listeners list
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param listener - [InstanceHandler] binding handler
     *
     */
    fun<T> removeListener(tag: KodiTagWrapper, scope: KodiScopeWrapper, listener: InstanceHandler<T>, event: BindingEvent = BindingEvent.BIND): Boolean

    /**
     * Clear all binding listeners by input parameters
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     *
     */
    fun removeAllListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper)

    /**
     * Notify all binding listeners that instance is already added to dependency graph
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param instance - current instance of listening
     *
     */
    fun <T> notifyListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper, instance: T, event: BindingEvent = BindingEvent.BIND)
}

/**
 * Main instance and scopes class
 */
abstract class KodiStorage : IKodiStorage<KodiHolder> {

    /**
     * All instances holders storage
     */
    private val instancesStore by immutableGetter { mutableMapOf<String, KodiHolder>() }

    /**
     *  Kodi modules set
     */
    private val modulesSet by immutableGetter { mutableSetOf<IKodiModule>() }

    /**
     * Instance Binding Listeners
     */
    private val bindingListeners by immutableGetter { mutableMapOf<String, Handlers>() }

    /**
     * Instance Unbind listeners
     */
    private val unbindingListeners by immutableGetter { mutableMapOf<String, Handlers>() }

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
        return tag.takeIf {
            it.isNotEmpty()
        }?.let {
            // if we has scope to delete instance, create key and remove current instance
            if(scope.isNotEmpty() && scope != defaultScope) {
                val key = createKey(tag, scope)
                instancesStore.remove(key)?.apply {
                    clear()
                } != null
            } else {
                val allInstances = takeFilteredKeys(tag.asString())
                allInstances.forEach {
                    instancesStore.remove(it)?.clear()
                }
                allInstances.isNotEmpty()
            }
        } ?: false
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
     * Add binding listeners to the list
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param listener - [InstanceHandler] function for handle binding event
     *
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> addListener(
            tag: KodiTagWrapper,
            scope: KodiScopeWrapper,
            listener: InstanceHandler<T>,
            event: BindingEvent
    ) {
        val key = createKey(tag, scope)
        val listeners = takeListeners(event).getOrPut(key) { mutableListOf() }
        val anyLocalListener = listener as AnyInstanceHandler
        if(listeners.indexOf(listener) < 0) {
            listeners.add(anyLocalListener)
        }
    }

    /**
     * Remove instance from binding listeners list
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     *
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> removeListener(
            tag: KodiTagWrapper,
            scope: KodiScopeWrapper,
            listener: InstanceHandler<T>,
            event: BindingEvent
    ): Boolean {
        val key = createKey(tag, scope)
        val listeners = takeListeners(event)[key]
        return listeners?.remove(listener as AnyInstanceHandler) ?: false
    }

    /**
     * Clear all binding listeners by input parameters
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     *
     */
    override fun removeAllListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper) {
        val key = createKey(tag, scope)
        bindingListeners[key]?.clear()
        unbindingListeners[key]?.clear()
    }

    /**
     * Check if instance has binding listeners
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     *
     */
    override fun hasListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper, event: BindingEvent): Boolean {
        val key = createKey(tag, scope)
        val listeners = takeListeners(event)[key]
        return listeners?.isNotEmpty() ?: false
    }

    /**
     * Notify all binding listeners that instance is already binding to dependency scope
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param instance - current instance of listening
     *
     */
    @Suppress("UNCHECKED_CAST")
    override fun<T> notifyListeners(
            tag: KodiTagWrapper,
            scope: KodiScopeWrapper,
            instance: T,
            event: BindingEvent
    ) {
        val key = createKey(tag, scope)
        val listeners = takeListeners(event)[key]
        val localListeners = listeners?.toList().orEmpty()
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
        bindingListeners.clear()
        unbindingListeners.clear()
    }

    /**
     * Filter [instanceStore] by some params
     * @param filter - current filter text
     */
    private fun takeFilteredKeys(filter: String): List<String> {
        return instancesStore.filterKeys { it.contains(filter) }.keys.toList()
    }

    /**
     * Take listeners from input event type
     */
    private fun takeListeners(event: BindingEvent): ListenersMap {
        return bindingListeners.takeIf { event == BindingEvent.BIND }
                .or { unbindingListeners }
    }

    /**
     * Create key for all data instances
     */
    private fun createKey(tag: KodiTagWrapper, scope: KodiScopeWrapper): String {
        val currentScope = scope.takeIf { it != defaultScope }.or { defaultScope  }
        return "${currentScope.asString()}_${tag.asString()}"
    }
}