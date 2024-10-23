package com.rasalexman.kodi.kmp.module

import com.rasalexman.kodi.kmp.common.defaultScope
import com.rasalexman.kodi.kmp.core.IKodiModule
import com.rasalexman.kodi.kmp.core.ModuleInitializer
import com.rasalexman.kodi.kmp.wrapper.KodiScopeWrapper
import com.rasalexman.kodi.kmp.wrapper.KodiTagWrapper


/**
 * Module implementation
 *
 * @param instanceInitializer - initializer [ModuleInitializer]
 */
internal data class KodiModule(
    override val instanceInitializer: ModuleInitializer
) : IKodiModule {
    /**
     * Module Scope. It's Lazy initializing
     */
    override var scope: KodiScopeWrapper = defaultScope

    /**
     * Set of all instances that includes in this module
     */
    override val moduleInstancesSet: MutableSet<KodiTagWrapper> by lazy { mutableSetOf() }
}
