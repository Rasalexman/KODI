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

import com.mincor.kodi.delegates.IImmutableDelegate
import com.mincor.kodi.delegates.IMutableDelegate
import com.mincor.kodi.delegates.immutableGetter
import com.mincor.kodi.delegates.mutableGetter

@Target(AnnotationTarget.FUNCTION)
annotation class CanThrowException(
        val message: String = "Check that the tag is added to the dependency graph, otherwise it will fall with RuntimeException"
)

/**
 * Main Singleton object for manipulate instances
 */
object Kodi : InstanceStorage<KodiHolder>(), IKodi

/**
 * Simple implementing interface for di functionality
 */
interface IKodi

/**
 * Initialize KODI dependencies
 *
 * @param block - main initialization block for binding instances
 */
inline fun initKODI(block: IKodi.() -> Unit): IKodi {
    Kodi.block()
    return Kodi
}

/**
 * Bind Any Generic type with some instance or KodiHolder types
 *
 * @param tag - optional parameter for custom manipulating with instance tag
 * if there is no tag provided the generic class name will be used as `T::class.toString()`
 */
inline fun <reified T : Any> IKodi.bind(tag: String? = null): KodiTagWrapper {
    return KodiTagWrapper(tag ?: T::class.toString())
}

/**
 * Unbind instance by given tag or generic type
 *
 * @param tag - optional parameter for custom manipulating with instance tag
 * if there is no tag provided the generic class name will be used as `T::class.toString()`
 */
inline fun <reified T : Any> IKodi.unbind(tag: String? = null): Boolean {
    val tagToWrapper = tag ?: T::class.toString()
    return Kodi.removeInstance(tagToWrapper.toTag())
}

/**
 * Take current instance for injection
 */
@CanThrowException
inline fun <reified T : Any> IKodi.instance(tag: String? = null): T {
    val type = tag ?: T::class.toString()
    return when (val holder = Kodi.createOrGet(type.toTag()) { throwException<RuntimeException>("There is no type $type in dependency map") }) {
        is KodiHolder.KodiSingle<*> -> holder.singleInstance as T
        is KodiHolder.KodiProvider<*> -> this.(holder.providerLiteral)() as T
        is KodiHolder.KodiConstant<*> -> holder.constantValue as T
    }
}

/**
 * Lazy immutable property initializer wrapper for injection
 * Example: `val someValue by immutableInstance<ISomeValueClass>()`
 * It cannot be change
 */
inline fun <reified T : Any> IKodi.immutableInstance(): IImmutableDelegate<T> = immutableGetter {
    instance<T>()
}

/**
 * Lazy mutable property initializer wrapper for injection
 * Example: `var someValue by mutableInstance<ISomeValueClass>()`
 * It can be change `someValue = object : ISomeValueClass {}`
 */
inline fun <reified T : Any> IKodi.mutableInstance(): IMutableDelegate<T> = mutableGetter {
    instance<T>()
}

/**
 * Helper throwable method
 *
 * @param message - Message for notify user in log
 */
inline fun<reified T : Exception> throwException(message: String): Nothing {
    val exception = when(T::class) {
        RuntimeException::class -> RuntimeException(message)
        else -> NoSuchElementException(message)
    }
    throw exception
}
