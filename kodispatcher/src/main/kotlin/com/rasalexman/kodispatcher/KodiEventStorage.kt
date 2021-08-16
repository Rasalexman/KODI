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
package com.rasalexman.kodispatcher

import com.rasalexman.kodi.core.KodiHolder
import com.rasalexman.kodi.core.KodiScopeWrapper
import com.rasalexman.kodi.core.KodiStorage
import com.rasalexman.kodi.core.KodiTagWrapper
import com.rasalexman.kodi.delegates.immutableGetter

/**
 * Instance Listener function
 */
internal typealias Handler<I> = (I) -> Unit

/**
 *
 */
internal typealias AnyKodiHolder = KodiHolder<Any>

/**
 *
 */
typealias KodiHolderHandler<T> = Handler<KodiHolder<T>>

/**
 * Any [KodiHolderHandler] type
 */
internal typealias AnyKodiHolderHandler = KodiHolderHandler<Any>

/**
 *
 */
internal typealias Handlers = MutableList<AnyKodiHolderHandler>

/**
 *
 */
internal typealias ListenersMap = MutableMap<String, Handlers>

/**
 *
 */
internal typealias EventsMap = MutableMap<KodiEvent, ListenersMap>

/**
 * Storage for events
 */
internal interface IKodiEventStorage<V> {
    /**
     * Add binding listeners to the list
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param listener - [Handler] function for handle binding event
     * @param event - [KodiEvent] event type
     */
    fun<T : Any> addListener(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        listener: KodiHolderHandler<T>,
        event: KodiEvent
    ): Boolean

    /**
     * Check if instance has binding listeners
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param event - [KodiEvent] event type
     *
     */
    fun hasListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper, event: KodiEvent): Boolean

    /**
     * Remove instance from binding listeners list
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param handler - [Handler] binding handler
     * @param event - [KodiEvent] event type
     *
     */
    fun<T : Any> removeListener(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        handler: KodiHolderHandler<T>,
        event: KodiEvent
    ): Boolean

    /**
     * Clear all binding listeners by input parameters
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param event - [KodiEvent] event type
     */
    fun removeAllListeners(tag: KodiTagWrapper, scope: KodiScopeWrapper, event: KodiEvent): Boolean

    /**
     * Notify all binding listeners that instance is already added to dependency graph
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param kodiHolder - [KodiHolder] with instance of listening
     * @param event - [KodiEvent] event type
     **/
     fun<T : Any> notifyListeners(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        kodiHolder: KodiHolder<T>,
        event: KodiEvent
    ): Boolean
}

/**
 *
 */
abstract class KodiEventStorage : KodiStorage(), IKodiEventStorage<KodiHolder<out Any>> {

    /**
     * Instance of [KodiEvent] Binding Listeners
     */
    private val eventsListener: EventsMap by immutableGetter { mutableMapOf() }

    /**
     * Add binding listeners to the list
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param listener - [Handler] function for handle events
     * @param event - [KodiEvent] event type
     */
    @Suppress("UNCHECKED_CAST")
    override fun<T : Any> addListener(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        listener: KodiHolderHandler<T>,
        event: KodiEvent
    ): Boolean {
       return (listener as? AnyKodiHolderHandler)?.let {
            val key = createKey(tag, scope)
            val listeners = takeHandler(event, key)
            if (listeners.indexOf(it) < 0) {
                listeners.add(it)
            } else false
        } ?: false
    }

    /**
     * Remove instance from binding listeners list
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param handler - [AnyKodiHolderHandler] function for handle events
     * @param event - [KodiEvent] event type
     *
     * @return [Boolean] told does references of [Handler] deleted successfully
     */
    @Suppress("UNCHECKED_CAST")
    override fun<T : Any> removeListener(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        handler: KodiHolderHandler<T>,
        event: KodiEvent
    ): Boolean {
        return (handler as? AnyKodiHolderHandler)?.run {
            try {
                eventsListener[event]?.let { listenersMap ->
                    val key = createKey(tag, scope)
                    val handlers = listenersMap[key]
                    handlers?.remove(this)
                }
            } catch (e: Exception) {
                println("------> Cannot `removeListener` with tag = $tag by event = $event and error = $e")
                false
            }
        } ?: false
    }

    /**
     * Clear all binding listeners by input parameters
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param event - [KodiEvent] event type
     */
    override fun removeAllListeners(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        event: KodiEvent
    ): Boolean {
        return try {
            eventsListener[event]?.let { listenersMap ->
                val key = createKey(tag, scope)
                listenersMap[key]?.clear()?.let {
                    eventsListener.remove(event)
                }
            } != null
        } catch (e: Exception) {
            println("[ERROR] Cannot `removeAllListeners` with tag = $tag and scope = $scope by event = $event and error = $e")
            false
        }
    }

    /**
     * Check if instance has binding listeners
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param event - [KodiEvent] event type
     *
     * @return [Boolean] does it has [KodiEvent] listener
     */
    override fun hasListeners(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        event: KodiEvent
    ): Boolean {
        val key = createKey(tag, scope)
        return hasListeners(event, key)
    }

    /**
     * Notify all binding listeners that instance is already binding to dependency scope
     *
     * @param tag - [KodiTagWrapper] to remove value if it's exist
     * @param scope - [KodiScopeWrapper] current scope data
     * @param kodiHolder - [KodiHolder] current instance of listening
     * @param event - [KodiEvent] event type
     */
    @Suppress("UNCHECKED_CAST")
    override fun<T : Any> notifyListeners(
        tag: KodiTagWrapper,
        scope: KodiScopeWrapper,
        kodiHolder: KodiHolder<T>,
        event: KodiEvent
    ): Boolean {
        return if(eventsListener.isNotEmpty()) {
            val anyKodiHolder = kodiHolder as? AnyKodiHolder
            anyKodiHolder?.let { holder ->
                val key = createKey(tag, scope)
                val handlers = takeHandler(event, key, true)
                val hasListeners = handlers.isNotEmpty()
                if(hasListeners) {
                    handlers.forEach { it.invoke(holder) }
                }
                hasListeners
            } ?: false
        } else false
    }


    /**
     * Remove all instances and scopes from dependency graph
     * Warning!!! - this action cannot be reverted
     */
    override fun clearAll() {
        super.clearAll()
        eventsListener.onEach { it.value.clear() }.clear()
    }

    /**
     *
     */
    override fun createKey(tag: KodiTagWrapper, scope: KodiScopeWrapper, withCopy: Boolean): String {
        return "${scope.asString()}_${tag.asString()}"
    }

    /**
     * Take handlers from input event type and key
     *
     * @param event - [KodiEvent] event type
     * @param key - instance name key
     * @param isCopy - need to make a copy of original collection, default: false
     *
     * @return [MutableList] of [KodiHolderHandler]
     */
    private fun takeHandler(
        event: KodiEvent,
        key: String,
        isCopy: Boolean = false
    ): Handlers {
        return takeListenersMap(event, isCopy).run {
            if (isCopy) this[key].orEmpty().toMutableList() else this.getOrPut(key) { mutableListOf() }
        }
    }

    /**
     * Take listeners from input event type
     *
     * @param event - [KodiEvent] event type
     * @param isCopy - need to make a copy of original collection, default: false
     *
     * @return [MutableMap] of [String] keys and [Handlers] list of listeners
     */
    private fun takeListenersMap(event: KodiEvent, isCopy: Boolean = false): ListenersMap {
        return eventsListener.run {
            if (isCopy) {
                this[event].orEmpty().toMutableMap()
            } else {
                this.getOrPut(event) { mutableMapOf() }
            }
        }
    }

    /**
     *
     */
    private fun hasListeners(event: KodiEvent, key: String): Boolean {
        return if(eventsListener.isNotEmpty()) {
            val listenersHandlers = takeListenersMap(event, true)[key]
            listenersHandlers != null && listenersHandlers.isNotEmpty()
        } else false
    }
}