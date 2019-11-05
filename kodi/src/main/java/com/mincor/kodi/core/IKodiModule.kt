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

/**
 * Module Initializer
 */
typealias ModuleInitializer = InstanceInitializer<Unit>

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
    val moduleHolders: MutableSet<String>

    /**
     * Set scope for all bindings in module
     *
     * @param scopeWrapper - [KodiScopeWrapper] for module
     */
    infix fun withScope(scopeWrapper: KodiScopeWrapper): IKodiModule {
        scope = scopeWrapper
        return this
    }
}

/**
 * Module implementation
 *
 * @param instanceInitializer - initializer [ModuleInitializer]
 * @param scope - current [emptyScope]
 * @param moduleHolders - all binding tags of this module
 */
data class KodiModule(
        override val instanceInitializer: ModuleInitializer,
        override var scope: KodiScopeWrapper = emptyScope(),
        override val moduleHolders: MutableSet<String> = mutableSetOf()
) : IKodiModule

/**
 * Kodi module implementation
 *
 * @param - [ModuleInitializer]
 */
fun kodiModule(init: ModuleInitializer): IKodiModule = KodiModule(init)

/**
 * Import [IKodiModule] into dependency graph or another module
 *
 * @param module - [IKodiModule]
 */
fun IKodi.import(module: IKodiModule) {
    Kodi.addModule(module)
}

/**
 * Unbind all instances in this module from dependency graph
 * also remove current module from KODI storage
 */
fun IKodiModule.remove() {
    Kodi.removeModule(this)
}