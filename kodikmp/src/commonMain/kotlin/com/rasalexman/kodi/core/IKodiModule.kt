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
package com.rasalexman.kodi.core

/**
 * Module Initializer
 */
internal typealias ModuleInitializer = InstanceInitializer<Unit>

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
    var scope: KodiKeyWrapper

    /**
     * Set of current module binding types
     */
    val moduleInstancesSet: MutableSet<KodiTagWrapper>
}

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
    override var scope: KodiKeyWrapper = defaultScope

    /**
     * Set of all instances that includes in this module
     */
    override val moduleInstancesSet: MutableSet<KodiTagWrapper> by lazy { mutableSetOf() }
}

/**
 * Set scope for all bindings in module
 *
 * @param scopeName - [String] scope name for module
 */
infix fun IKodiModule.withScope(scopeName: String): IKodiModule {
    scope = scopeName.asScope()
    return this
}

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
fun IKodi.import(module: IKodiModule)  {
    Kodi.addModule(module)
}

/**
 * Does this module contains input instance tag or class type
 *
 * @param tag [String] - string representation of instance must be unique
 */
inline fun <reified InstanceType : Any> IKodiModule.hasInstance(tag: String? = null): Boolean {
    val tagToWrapper = tag.asTag<InstanceType>()
    return this.moduleInstancesSet.contains(tagToWrapper)
}

/**
 * Unbind all instances in this module from dependency graph
 * also remove current module from KODI storage
 */
fun IKodiModule.remove() {
    Kodi.removeModule(this)
}