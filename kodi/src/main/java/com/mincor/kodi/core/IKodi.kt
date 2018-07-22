package com.mincor.kodi.core

import com.mincor.kodi.ext.className
import com.mincor.kodi.ext.createInstance
import com.mincor.kodi.ext.injectInConstructor
import com.mincor.kodi.ext.toValMap
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

/**
 * Provider holder data class is a function holder with params for future or immediately call
 */
data class ProviderHolder<T>(var function: KFunction<T>? = null, var params: MutableMap<KParameter, Any>? = null) {
    /**
     * Call the function with given params
     */
    fun call(newParams:List<Any>? = null): T {
        params = newParams?.toValMap(function?.parameters) ?: params ?: mutableMapOf()
        return function?.callBy(params!!)!!
    }

    /**
     * Clear all references from object
     */
    fun clear() {
        function = null
        params = null
    }
}

/**
 * Main Singleton object for manipulate instances
 */
private object Kodi : IMapper<Any> {
    override val instanceMap: MutableMap<String, Any> = mutableMapOf()
}

/**
 * Simple implementing interface for di functionality
 */
interface IKodi

/**
 * GET a single object reference, it garanted that you have only one instance of an given generic class
 */
inline fun <reified T : Any> IKodi.single(params: List<Any>? = null): T {
    return Kodi.createOrGet(T::class.className()) {
        T::class.createInstance(params)
    }.injectInConstructor(params) as T
}

/**
 * Create an instance of a given generic class without saving to instanceMap
 */
inline fun <reified T : Any> IKodi.instance(vararg params: Any): T {
    return T::class.createInstance(params.asList())
}

/**
 * Create an instance of a given generic class and save it to instanceMap by tag name
 * @param tag
 * Tag name for instance to save
 *
 * @param params
 * Constructor params
 */
inline fun <reified T : Any> IKodi.instanceByTag(tag:String, vararg params: Any): T {
    return Kodi.createOrGet(tag) {
        T::class.createInstance(params.asList())
    }.injectInConstructor(params.asList()) as T
}

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
inline fun <reified T : Any> IKodi.provider(tag: String = "", function: KFunction<T>? = null, params: List<Any>? = null): ProviderHolder<T> {
    if(!Kodi.has(tag) && function == null) throw RuntimeException("There is no provider for given tag '$tag'. You must set function for provider")
    val valmap = params?.toValMap(function?.parameters)
    val key = if (tag.isEmpty()) function?.name ?: throw RuntimeException("There is no provider for given tag '$tag'. You must set function for provider") else tag
    return Kodi.createOrGet(key) {
        ProviderHolder(function, valmap)
    } as ProviderHolder<T>
}

/**
 * immediately calls the function with given params. If provider doesnt exist its create one and make call
 */
inline fun <reified T : Any> IKodi.providerCall(tag: String = "", function: KFunction<T>? = null, vararg params: Any): T {
    return provider(tag, function).call(params.asList())
}

/**
 * immediately calls the function with given params. If provider doesnt exist its create one and make call
 */
inline fun <reified T : Any> IKodi.providerCallByTag(tag: String = "", vararg params: Any): T {
    return provider<T>(tag).call(params.asList())
}


/**
 * Check if an instance from store by given tag or generic class exist
 */
inline fun <reified T : Any> IKodi.has(tag:String? = null) {
    Kodi.has(tag?:T::class.className())
}

/**
 * Remove an instance from store by given tag or generic class
 */
inline fun <reified T : Any> IKodi.remove(tag:String? = null) {
    Kodi.remove(tag?:T::class.className())
}

/**
 * Remove all instances from store
 */
fun IKodi.removeAll() {
    Kodi.removeAll()
}

/**
 * Lazy implementation of single<T>()
 */
inline fun <reified T : Any> IKodi.singleLazy(vararg params: Any): Lazy<T> = lazy {
    this.single<T>(params.asList())
}

/**
 * Lazy implementation of provider<T>()
 */
inline fun <reified T : Any> IKodi.providerLazy(tag: String = "", function: KFunction<T>? = null, vararg params: Any): Lazy<ProviderHolder<T>> = lazy {
    this.provider(tag, function, params.asList())
}
