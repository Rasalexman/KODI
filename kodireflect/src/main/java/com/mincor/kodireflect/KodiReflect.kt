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

package com.mincor.kodireflect
import com.mincor.kodireflect.ext.*
import kotlin.reflect.KFunction


/**
 * Main Singleton object for manipulate instances
 */
object KodiReflect : IMapper<Any>, IKodiReflect {
    /**
     * Instance map for holding any type of dependencies
     */
    override val instanceStorage = mutableMapOf<String, Any>()
}

/**
 * Helper interface for bind generic instances with name
 */
interface IMapper<T : Any> {
    /**
     * Main entities(instances) holder map
     */
    val instanceStorage: MutableMap<String, T>
}

/**
 * Simple implementing interface for di functionality
 */
interface IKodiReflect

/**
 * Remove all instances from store
 */
fun IKodiReflect.removeAll() {
    (KodiReflect as IMapper<Any>).removeAll()
}

/**
 * Initialize KODIReflect dependencies
 */
inline fun initKODIReflect(block: IKodiReflect.() -> Unit): IKodiReflect {
    KodiReflect.block()
    return KodiReflect
}

/**
 *  Remove all instances from map
 */
inline fun <reified T : Any> IMapper<T>.removeAll() {
    instanceStorage.forEach { (_, instance) ->
        (instance as? ProviderHolder<T?>)?.clear()
    }
    instanceStorage.clear()
}

/**
 * Remove instance from map by given key
 *
 * @param key
 * Key to remove instance
 */
inline fun <reified T : Any> KodiReflect.removeInstance(key: String) {
    if (hasInstance(key)) (this.instanceStorage.remove(key) as? ProviderHolder<T?>)?.clear()
}


/**
 * Bind Key to Value type and return an instance of Value
 *
 * @param tag - current constant tag
 * @param value - constant value
 */
inline fun <reified T : Any> IKodiReflect.constant(
        tag: String,
        value: T = T::class.createInstance()
): T = KodiReflect.createOrGet(tag) { value } as T

/**
 * Bind Key to Value type and return an instance of Value
 *
 * @param params - [] of input params
 */
inline fun <reified K : Any, reified V : Any> IKodiReflect.bind(vararg params: Any): V {
    return KodiReflect.createOrGet(K::class.className()) {
        V::class.createInstance(params.toList())
    }.injectInConstructor(params.toList()) as V
}

/**
 * GET a single object reference, it garanted that you have only one instance of an given generic class
 */
inline fun <reified T : Any> IKodiReflect.single(vararg params: Any): T = this.singleWithList(params.toList())

/**
 * It's provide a lambda to create single saved instance
 *
 * @param params - [] of input params
 */
inline fun <reified T : Any> IKodiReflect.singleProvider(crossinline block: () -> T): T = KodiReflect.createOrGet(T::class.className()) {
    block()
} as T

/**
 * GET a single object reference, it garanted that you have only one instance of an given generic class
 *
 * @param params - [] of input params
 */
inline fun <reified T : Any> IKodiReflect.singleWithList(params: List<Any>? = null): T = KodiReflect.createOrGet(T::class.className()) {
    T::class.createInstance(params)
}.injectInConstructor(params) as T

/**
 * Create an instance of a given generic class without saving to instanceStorage
 *
 * @param params - [] of input params
 */
inline fun <reified T : Any> IKodiReflect.instance(vararg params: Any): T = T::class.createInstance(params.asList())

/**
 * Create an instance of a given generic class and save it to instanceStorage by tag name
 * @param tag
 * Tag name for instance to save
 *
 * @param params
 * Constructor params
 */
inline fun <reified T : Any> IKodiReflect.instanceByTag(tag: String, vararg params: Any): T = KodiReflect.createOrGet(tag) {
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
inline fun <reified T : Any> IKodiReflect.provider(tag: String = "", function: KFunction<T?>? = null, params: List<Any>? = null): ProviderHolder<T?> {
    if (!KodiReflect.hasInstance(tag) && function == null) throw Exceptions.ProviderException(tag)
    val valmap = params?.toValMap(function?.parameters)
    val key = if (tag.isEmpty()) function?.name
            ?: throw Exceptions.ProviderException(tag) else tag
    return KodiReflect.createOrGet(key) {
        ProviderHolder(function, valmap)
    } as ProviderHolder<T?>
}

/**
 * immediately calls the function with given params. If provider doesnt exist its create one and make call
 */
inline fun <reified T : Any> IKodiReflect.providerCall(tag: String = "", function: KFunction<T>? = null, vararg params: Any): T? {
    return provider(tag, function).call(params.asList())
}

/**
 * immediately calls the function with given params. If provider doesnt exist its create one and make call
 */
inline fun <reified T : Any> IKodiReflect.providerCallByTag(tag: String = "", vararg params: Any): T? {
    return provider<T>(tag).call(params.asList())
}


/**
 * Check if an instance from store by given tag or generic class exist
 */
inline fun <reified T : Any> IKodiReflect.has(tag: String? = null) {
    KodiReflect.hasInstance(tag ?: T::class.className())
}

/**
 * Remove an instance from store by given tag or generic class
 */
inline fun <reified T : Any> IKodiReflect.remove(tag: String? = null) {
    KodiReflect.removeInstance<T>(tag ?: T::class.className())
}

/**
 * Lazy implementation of single<T>()
 */
inline fun <reified T : Any> IKodiReflect.singleLazy(vararg params: Any): Lazy<T> = kotlin.lazy {
    this.singleWithList<T>(params.asList())
}

/**
 * Lazy implementation of instance<T>()
 */
inline fun <reified T : Any> IKodiReflect.instanceLazy(vararg params: Any): Lazy<T> = kotlin.lazy {
    T::class.createInstance(params.asList())
}

/**
 * Lazy implementation of instanceByTag<T>()
 */
inline fun <reified T : Any> IKodiReflect.instanceLazyByTag(tag: String, vararg params: Any): Lazy<T> = kotlin.lazy {
    this.instanceByTag<T>(tag, params.asList())
}

/**
 * Lazy implementation of provider<T>()
 */
inline fun <reified T : Any> IKodiReflect.providerLazy(tag: String = "", function: KFunction<T?>? = null, vararg params: Any): Lazy<ProviderHolder<T?>> = kotlin.lazy {
    this.provider(tag, function, params.asList())
}

/**
 * Mutable Lazy implementation of single<T>()
 */
inline fun <reified T : Any> IKodiReflect.singleMutableLazy(vararg params: Any): MutableLazy<T?> = MutableLazy {
    this.singleWithList<T>(params.asList())
}

/**
 * MutableLazy implementation of instance<T>()
 *
 * @param params - vararg for inject into created instance constructor
 */
inline fun <reified T : Any> IKodiReflect.instanceMutableLazy(vararg params: Any): MutableLazy<T?> = MutableLazy {
    T::class.createInstance(params.asList())
}

/**
 * MutableLazy implementation of provider<T>()
 *
 * @param tag - tag for mapping
 * @param function - function for initialization
 * @param params - additional parameters to inject
 */
inline fun <reified T : Any> IKodiReflect.providerMutableLazy(
        tag: String = "",
        function: KFunction<T>? = null,
        vararg params: Any
): MutableLazy<ProviderHolder<T?>> = MutableLazy {
    this.provider(tag, function, params.asList())
}