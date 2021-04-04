package com.rasalexman.kodi.core

/**
 * Add listener for event when instance is already binded to dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 *
 */
inline fun <reified T : Any> IKodi.addBindingListener(
        tag: String? = null,
        scope: String? = null,
        noinline listener: InstanceHandler<T>
) {
    addListener(tag, scope, listener, BindingEvent.BIND)
}

/**
 * Remove listener for event when instance is already binded to dependency graph
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function (Optional),
 * if there is a null, it remove all binding listeners for selected tad and scope
 *
 */
inline fun <reified T : Any> IKodi.removeBindingListener(
        tag: String? = null,
        scope: String? = null,
        noinline listener: InstanceHandler<T>?
) {
    removeListener(tag, scope, listener, BindingEvent.BIND)
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
        noinline listener: InstanceHandler<T>
) {
    addListener(tag, scope, listener, BindingEvent.UNBIND)
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
        noinline listener: InstanceHandler<T>?
) {
    removeListener(tag, scope, listener, BindingEvent.UNBIND)
}

/**
 * Add listener for bind or unbind event for instance
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function
 * @param event - [BindingEvent]
 *
 */
inline fun <reified T : Any> IKodi.addListener(
        tag: String? = null,
        scope: String? = null,
        noinline listener: InstanceHandler<T>,
        event: BindingEvent
) {
    val tagToWrap = tag.or { genericName<T>() }.asTag()
    val scopeToWrap = scope?.asScope()
    Kodi.addListener(tagToWrap, scopeToWrap.or { defaultScope }, listener, event)
}

/**
 * Remove listener for bind or unbind event for instance
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param listener - event handling function (Optional), that was listen the event
 * @param event - [BindingEvent]
 *
 */
inline fun <reified T : Any> IKodi.removeListener(
        tag: String? = null,
        scope: String? = null,
        noinline listener: InstanceHandler<T>?,
        event: BindingEvent
) {
    val tagToWrap = tag.or { genericName<T>() }.asTag()
    val scopeToWrap = scope?.asScope()
    listener?.let {
        Kodi.removeListener(tagToWrap, scopeToWrap.or { defaultScope }, listener, event)
    }.or {
        Kodi.removeAllListeners(tagToWrap, scopeToWrap.or { defaultScope })
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
    return hasEventListener<T>(tag, scope, BindingEvent.BIND)
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
    return hasEventListener<T>(tag, scope, BindingEvent.UNBIND)
}

/**
 * Check has listeners for specific [BindingEvent]
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope (Optional)
 * @param event - [BindingEvent]
 *
 */
inline fun <reified T : Any> IKodi.hasEventListener(
        tag: String? = null,
        scope: String? = null,
        event: BindingEvent
): Boolean {
    val tagToWrap = tag.or { genericName<T>() }.asTag()
    val scopeToWrap = scope?.asScope()
    return Kodi.hasListeners(tagToWrap, scopeToWrap.or { defaultScope }, event)
}

