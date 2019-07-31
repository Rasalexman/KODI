// Copyright (c) 2018 Aleksandr Minkin (sphc@yandex.ru)
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

import com.mincor.kodi.ext.*
import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance

/**
 * Main Singleton object for manipulate instances
 */
object Kodi : IMapper<Any>, IKodi {
    /**
     * Instance map for holding any type of dependencies
     */
    override val instanceMap: MutableMap<String, Any> = mutableMapOf()
}

/**
 * Simple implementing interface for di functionality
 */
interface IKodi

/**
 * Initialize KODI dependencies
 */
inline fun initKODI(block: IKodi.() -> Unit): IKodi {
    block(Kodi)
    return Kodi
}

/**
 * Remove all instances from store
 */
fun IKodi.removeAll() {
    Kodi.removeAll()
}

/**
 * Bind Key to Value type and return an instance of Value
 */
inline fun <reified T : Any> IKodi.constant(tag: String, value: T = T::class.createInstance()): T = Kodi.createOrGet(tag) {
        value
    } as T

/**
 * Bind Key to Value type and return an instance of Value
 */
inline fun <reified K : Any, reified V : Any> IKodi.bind(vararg params: Any): V {
    return Kodi.createOrGet(K::class.className()) {
        V::class.createInstance(params.toList())
    }.injectInConstructor(params.toList()) as V
}

/**
 * GET a single object reference, it garanted that you have only one instance of an given generic class
 */
inline fun <reified T : Any> IKodi.single(vararg params: Any): T = this.singleWithList(params.toList())

/**
 * It's provide a lambda to create single saved instance
 */
inline fun <reified T : Any> IKodi.singleProvider(block: () -> T): T = Kodi.createOrGet(T::class.className()) {
        block()
    } as T

/**
 * GET a single object reference, it garanted that you have only one instance of an given generic class
 */
inline fun <reified T : Any> IKodi.singleWithList(params: List<Any>? = null): T = Kodi.createOrGet(T::class.className()) {
        T::class.createInstance(params)
    }.injectInConstructor(params) as T

/**
 * Create an instance of a given generic class without saving to instanceMap
 */
inline fun <reified T : Any> IKodi.instance(vararg params: Any): T = T::class.createInstance(params.asList())

/**
 * Create an instance of a given generic class and save it to instanceMap by tag name
 * @param tag
 * Tag name for instance to save
 *
 * @param params
 * Constructor params
 */
inline fun <reified T : Any> IKodi.instanceByTag(tag: String, vararg params: Any): T = Kodi.createOrGet(tag) {
        T::class.createInstance(params.asList())
    }.injectInConstructor(params.asList()) as T

/**
 * Gives to us an instance of ProviderHolder class that store a reference to function and parameters for future calls
 * @param tag
 * Tag name for instance to save
 *
 * @param function
 * This is a function for call later. If there is no tag or function does not exist in current instanceStore it's throw an Error
 *
 * @param params
 * function params as listOf<Any>(...)
 */
inline fun <reified T : Any> IKodi.provider(tag: String = "", function: KFunction<T?>? = null, params: List<Any>? = null): ProviderHolder<T?> {
    if (!Kodi.hasInstance(tag) && function == null) throw RuntimeException("There is no provider for given tag '$tag'. You must set function for provider")
    val valmap = params?.toValMap(function?.parameters)
    val key = if (tag.isEmpty()) function?.name
            ?: throw RuntimeException("There is no provider for given tag '$tag'. You must set function for provider") else tag
    return Kodi.createOrGet(key) {
        ProviderHolder(function, valmap)
    } as ProviderHolder<T?>
}

/**
 * immediately calls the function with given params. If provider doesnt exist its create one and make call
 */
inline fun <reified T : Any> IKodi.providerCall(tag: String = "", function: KFunction<T>? = null, vararg params: Any): T? {
    return provider(tag, function).call(params.asList())
}

/**
 * immediately calls the function with given params. If provider doesnt exist its create one and make call
 */
inline fun <reified T : Any> IKodi.providerCallByTag(tag: String = "", vararg params: Any): T? {
    return provider<T>(tag).call(params.asList())
}


/**
 * Check if an instance from store by given tag or generic class exist
 */
inline fun <reified T : Any> IKodi.has(tag: String? = null) {
    Kodi.hasInstance(tag ?: T::class.className())
}

/**
 * Remove an instance from store by given tag or generic class
 */
inline fun <reified T : Any> IKodi.remove(tag: String? = null) {
    Kodi.removeInstance(tag ?: T::class.className())
}

/**
 * Lazy implementation of single<T>()
 */
inline fun <reified T : Any> IKodi.singleLazy(vararg params: Any): Lazy<T> = lazy {
    this.singleWithList<T>(params.asList())
}

/**
 * Lazy implementation of instance<T>()
 */
inline fun <reified T : Any> IKodi.instanceLazy(vararg params: Any): Lazy<T> = lazy {
    T::class.createInstance(params.asList())
}

/**
 * Lazy implementation of instanceByTag<T>()
 */
inline fun <reified T : Any> IKodi.instanceLazyByTag(tag: String, vararg params: Any): Lazy<T> = lazy {
    this.instanceByTag<T>(tag, params.asList())
}

/**
 * Lazy implementation of provider<T>()
 */
inline fun <reified T : Any> IKodi.providerLazy(tag: String = "", function: KFunction<T?>? = null, vararg params: Any): Lazy<ProviderHolder<T?>> = lazy {
    this.provider(tag, function, params.asList())
}

/**
 * Mutable Lazy implementation of single<T>()
 */
inline fun <reified T : Any> IKodi.singleMutableLazy(vararg params: Any): MutableLazy<T?> = MutableLazy {
    this.singleWithList<T>(params.asList())
}

/**
 * MutableLazy implementation of instance<T>()
 *
 * @param params - vararg for inject into created instance constructor
 */
inline fun <reified T : Any> IKodi.instanceMutableLazy(vararg params: Any): MutableLazy<T?> = MutableLazy {
    T::class.createInstance(params.asList())
}

/**
 * MutableLazy implementation of provider<T>()
 *
 * @param tag - tag for mapping
 * @param function - function for initialization
 * @param params - additional parameters to inject
 */
inline fun <reified T : Any> IKodi.providerMutableLazy(
        tag: String = "",
        function: KFunction<T>? = null,
        vararg params: Any
): MutableLazy<ProviderHolder<T?>> = MutableLazy {
    this.provider(tag, function, params.asList())
}
