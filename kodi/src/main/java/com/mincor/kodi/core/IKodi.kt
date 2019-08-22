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
    Kodi.block()
    return Kodi
}

inline class KodiTypeWrapper(private val type: String) : IKodi {
    infix fun with(instance: KodiHolder) {
        Kodi.createOrGet(type) { instance }
    }
}

sealed class KodiHolder {
    data class KodiSingle<T : Any>(val singleInstanceProvider: IKodi.() -> T) : KodiHolder() {
        val singleInstance: T by lazy { Kodi.singleInstanceProvider() }
    }
    data class KodiProvider<T : Any>(val providerLiteral: IKodi.() -> T) : KodiHolder()
    data class KodiConstant<T : Any>(val constantValue: T) : KodiHolder()
}

inline fun <reified T : Any> IKodi.bind(tag: String? = null): KodiTypeWrapper {
    return KodiTypeWrapper(tag ?: T::class.toString())
}

inline fun <reified T : Any> IKodi.single(noinline init: IKodi.() -> T): KodiHolder {
    return KodiHolder.KodiSingle(init)
}

inline fun <reified T : Any> IKodi.provider(noinline init: IKodi.() -> T): KodiHolder {
    return KodiHolder.KodiProvider(init)
}

inline fun <reified T : Any> IKodi.constant(noinline init: IKodi.() -> T): KodiHolder {
    return KodiHolder.KodiConstant(init())
}

inline fun <reified T : Any> IKodi.instance(tag: String? = null): T {
    val type = tag ?: T::class.toString()
    return when (val holder = Kodi.createOrGet(type) { throw RuntimeException("There is no type $type in dependency map") } as KodiHolder) {
        is KodiHolder.KodiSingle<*> -> holder.singleInstance as T
        is KodiHolder.KodiProvider<*> -> this.(holder.providerLiteral)() as T
        is KodiHolder.KodiConstant<*> -> holder.constantValue as T
    }
}

inline fun <reified T : Any> IKodi.instanceLazy(): Lazy<T> = lazy {
    instance<T>()
}
