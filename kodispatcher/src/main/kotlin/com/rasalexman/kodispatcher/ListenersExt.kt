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

@file:Suppress("unused")

package com.rasalexman.kodispatcher

import com.rasalexman.kodi.core.asScope
import com.rasalexman.kodi.core.asTag
import com.rasalexman.kodi.core.or
import com.rasalexman.kodi.core.provider

/**
 * Add listener for event when instance is already binded to dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function [KodiHolderHandler]
 *
 */
inline fun <reified T : Any> IKodiListener.addBindingListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>
) {
    addListener(KodiEvent.BIND, tag, scope, listener)
}

/**
 * Remove listener for event when instance is already binded to dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function (Optional),
 * if there is a null, it remove all binding listeners for selected tag and scope
 *
 */
inline fun <reified T : Any> IKodiListener.removeBindingListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>? = null
) {
    removeListener(KodiEvent.BIND, tag, scope, listener)
}

/**
 * Add listener for event when instance is already unbound(removed) from dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 *
 */
inline fun <reified T : Any> IKodiListener.addUnbindingListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>
) {
    addListener(KodiEvent.UNBIND, tag, scope, listener)
}

/**
 * Remove listener for event when instance is already removed to dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function (Optional),
 * if there is a null, it remove all unbinding listeners for selected tad and scope
 *
 */
inline fun <reified T : Any> IKodiListener.removeUnbindingListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>?
) {
    removeListener(KodiEvent.UNBIND, tag, scope, listener)
}

/**
 * Add listener for event when instance is invoke from dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 *
 */
inline fun <reified T : Any> IKodiListener.addInstanceListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>
) {
    addListener(KodiEvent.INSTANCE, tag, scope, listener)
}

/**
 * Remove listener for event when instance is invoke from dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 *
 */
inline fun <reified T : Any> IKodiListener.removeInstanceListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>
) {
    removeListener(KodiEvent.INSTANCE, tag, scope, listener)
}

/**
 * Add listener for bind or unbind event for instance
 *
 * @param event - [KodiEvent]
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 *
 */
inline fun <reified T : Any> IKodiListener.addListener(
    event: KodiEvent,
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>,
) {
    val tagToWrap = (tag?:this).asTag<T>()
    val scopeToWrap = scope.asScope()
    KodiListener.addListener(tagToWrap, scopeToWrap, listener, event)
}

/**
 * Remove listener for bind or unbind event for instance
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function (Optional), that was listen the event
 * @param event - [KodiEvent]
 *
 */
inline fun <reified T : Any> IKodiListener.removeListener(
    event: KodiEvent,
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>? = null
) {
    val tagToWrap = (tag?:this).asTag<T>()
    val scopeToWrap = scope.asScope()
    listener?.let {
        KodiListener.removeListener(tagToWrap, scopeToWrap, it, event)
    }.or {
        KodiListener.removeAllListeners(tagToWrap, scopeToWrap, event)
    }
}

/**
 * Notify listener for [KodiEvent] event with data instance
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param data - Any type jf data to invoke lazy
 * @param event - [KodiEvent]
 *
 */
inline fun <reified T : Any> IKodiListener.notifyListener(
    event: KodiEvent,
    data: T,
    tag: String? = null,
    scope: String? = null
) {
    val tagToWrap = (tag?:this).asTag<T>()
    val scopeToWrap = scope.asScope()
    val provider = KodiListener.provider { data }
    KodiListener.notifyListeners(tagToWrap, scopeToWrap, provider, event)
}

/**
 * Check for binding listeners
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 *
 */
inline fun <reified T : Any> IKodiListener.hasBindingListener(
    tag: String? = null,
    scope: String? = null
): Boolean {
    return hasEventListener<T>(KodiEvent.BIND, tag, scope)
}

/**
 * Check for unbinding listeners
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 *
 */
inline fun <reified T : Any> IKodiListener.hasUnbindingListener(
    tag: String? = null,
    scope: String? = null
): Boolean {
    return hasEventListener<T>(KodiEvent.UNBIND, tag, scope)
}

/**
 * Check for binding listeners
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 *
 * @return [Boolean] told does it has active listener
 */
inline fun <reified T : Any> IKodiListener.hasInstanceListener(
    tag: String? = null,
    scope: String? = null
): Boolean {
    return hasEventListener<T>(KodiEvent.INSTANCE, tag, scope)
}

/**
 * Check has listeners for specific [KodiEvent]
 *
 * @param event - [KodiEvent]
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 *
 * @return [Boolean] true if has listener
 */
inline fun <reified T : Any> IKodiListener.hasEventListener(
    event: KodiEvent,
    tag: String? = null,
    scope: String? = null
): Boolean {
    val tagToWrap = (tag?:this).asTag<T>()
    val scopeToWrap = scope.asScope()
    return KodiListener.hasListeners(tagToWrap, scopeToWrap, event)
}



