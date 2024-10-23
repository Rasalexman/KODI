package com.rasalexman.kodi.kmp.core

import com.rasalexman.kodi.kmp.wrapper.KodiScopeWrapper
import com.rasalexman.kodi.kmp.wrapper.KodiTagWrapper

/**
 * Support Kodi Module
 */
interface IKodiModule : IKodi {
    /**
     * Initialization literal function withScope receiver
     */
    val instanceInitializer: ModuleInitializer

    /**
     * Module Scope for all elements
     */
    var scope: KodiScopeWrapper

    /**
     * Set of current module binding types
     */
    val moduleInstancesSet: MutableSet<KodiTagWrapper>
}