package com.rasalexman.kodi.core

/**
 * Add listener for event when instance is already binded to dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function [KodiHolderHandler]
 *
 */
inline fun <reified T : Any> IKodi.addBindingListener(
        tag: String? = null,
        scope: String? = null,
        noinline listener: KodiHolderHandler<T>
) {
    addListener(tag, scope, listener, KodiEvent.BIND)
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
inline fun <reified T : Any> IKodi.removeBindingListener(
        tag: String? = null,
        scope: String? = null,
        noinline listener: KodiHolderHandler<T>? = null
) {
    removeListener(tag, scope, listener, KodiEvent.BIND)
}

/**
 * Add listener for event when instance is already unbinded(removed) from dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 *
 */
inline fun <reified T : Any> IKodi.addUnbindingListener(
        tag: String? = null,
        scope: String? = null,
        noinline listener: KodiHolderHandler<T>
) {
    addListener(tag, scope, listener, KodiEvent.UNBIND)
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
inline fun <reified T : Any> IKodi.removeUnbindingListener(
        tag: String? = null,
        scope: String? = null,
        noinline listener: KodiHolderHandler<T>?
) {
    removeListener(tag, scope, listener, KodiEvent.UNBIND)
}

/**
 * Add listener for event when instance is invoke from dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 *
 */
inline fun <reified T : Any> IKodi.addInstanceListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>
) {
    addListener(tag, scope, listener, KodiEvent.INSTANCE)
}

/**
 * Remove listener for event when instance is invoke from dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 *
 */
inline fun <reified T : Any> IKodi.removeInstanceListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>
) {
    removeListener(tag, scope, listener, KodiEvent.INSTANCE)
}

/**
 * Add listener for bind or unbind event for instance
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 * @param event - [KodiEvent]
 *
 */
inline fun <reified T : Any> IKodi.addListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>,
    event: KodiEvent
) {
    val tagToWrap = tag.asTag<T>()
    val scopeToWrap = scope.asScope()
    Kodi.addListener(tagToWrap, scopeToWrap, listener, event)
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
inline fun <reified T : Any> IKodi.removeListener(
    tag: String? = null,
    scope: String? = null,
    noinline listener: KodiHolderHandler<T>? = null,
    event: KodiEvent
) {
    val tagToWrap = tag.asTag<T>()
    val scopeToWrap = scope.asScope()
    listener?.let {
        Kodi.removeListener(tagToWrap, scopeToWrap, it, event)
    }.or {
        Kodi.removeAllListeners(tagToWrap, scopeToWrap, event)
    }
}

/**
 * Check for binding listeners
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 *
 */
inline fun <reified T : Any> IKodi.hasBindingListener(
        tag: String? = null,
        scope: String? = null
): Boolean {
    return hasEventListener<T>(tag, scope, KodiEvent.BIND)
}

/**
 * Check for unbinding listeners
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 *
 */
inline fun <reified T : Any> IKodi.hasUnbindingListener(
        tag: String? = null,
        scope: String? = null
): Boolean {
    return hasEventListener<T>(tag, scope, KodiEvent.UNBIND)
}

/**
 * Check for binding listeners
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 *
 * @return [Boolean] told does it has active listener
 */
inline fun <reified T : Any> IKodi.hasInstanceListener(
    tag: String? = null,
    scope: String? = null
): Boolean {
    return hasEventListener<T>(tag, scope, KodiEvent.INSTANCE)
}

/**
 * Check has listeners for specific [KodiEvent]
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param event - [KodiEvent]
 *
 */
inline fun <reified T : Any> IKodi.hasEventListener(
        tag: String? = null,
        scope: String? = null,
        event: KodiEvent
): Boolean {
    val tagToWrap = tag.asTag<T>()
    val scopeToWrap = scope.asScope()
    return Kodi.hasListeners(tagToWrap, scopeToWrap, event)
}

