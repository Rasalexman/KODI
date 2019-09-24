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

/**
 * Annotation for mark some throwable functions
 *
 * @param message - string for user log output
 */
@Target(AnnotationTarget.FUNCTION)
annotation class CanThrowException(
        val message: String = "Check that the tag is added to the dependency graph, otherwise it will fall withScope RuntimeException"
)

/**
 * Main Singleton object for manipulate instances
 */
object Kodi : KodiStorage(), IKodi

/**
 * Simple implementing interface for di functionality
 */
interface IKodi

/**
 * Initialize KODI dependencies
 *
 * @param block - main initialization block for binding instances
 */
inline fun <reified T : Any> kodi(block: IKodi.() -> T): T {
    return Kodi.block()
}

/**
 * Bind Any Generic type withScope some instance or KodiHolder types
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.java.toString()`
 *
 * @receiver [IKodi]
 * @return [KodiTagWrapper]
 */
inline fun <reified T : Any> IKodi.bind(tag: String? = null): KodiTagWrapper {
    return (tag ?: "${T::class.java}").asTag()
}

/**
 * Bind Typed Instance to inherits type like <ISimpleInterface, BaseSimpleInterfaceImplementation>
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.java.toString()`
 *
 * @receiver [IKodi]
 * @return [KodiTagWrapper]
 */
inline fun <reified T : Any, reified R : T> IKodi.bindType(tag: String?= null): KodiTagWrapper {
    return (tag ?: "${R::class.java}").asTag()
}

/**
 * Unbind instance by given tag or generic type
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.toString()`
 */
inline fun <reified T : Any> IKodi.unbind(tag: String? = null): Boolean {
    val tagToWrapper = (tag ?: "${T::class.java}").asTag()
    return Kodi.removeInstance(tagToWrapper)?.apply { at(emptyScope()) } != null
}

/**
 * Unbind moduleScope and all instances from dependency graph
 *
 * @param scopeTagWrapper - [KodiScopeWrapper] to remove
 */
fun IKodi.unbindScope(scopeTagWrapper: KodiScopeWrapper): Boolean {
    return Kodi.removeAllScope(scopeTagWrapper)
}

/**
 * Unbind moduleScope and all instances from dependency graph
 *
 * @param scopeTagWrapper - [KodiScopeWrapper] to remove
 */
fun IKodi.unbindAll() {
    return Kodi.clearAll()
}

/**
 * Take current instance for injection
 *
 * @param tag - String instance tag (Optional)
 * @throws IllegalAccessException - if there is no tag in dependency graph
 */
@CanThrowException("There is no KodiHolder instance in dependency graph")
inline fun <reified T : Any> IKodi.instance(tag: String? = null): T {
    return holder<T>(tag).collect(this)
}

/**
 * Take current [KodiHolder] instance for injection or throw an Exception if it's does't exist
 *
 * @param tag - String instance tag (Optional)
 * @throws IllegalAccessException - if there is no tag in dependency graph it's crash
 */
@CanThrowException("There is no KodiHolder instance in dependency graph")
inline fun <reified T : Any> IKodi.holder(tag: String? = null): KodiHolder {
    val instance = this
    val tagToWrap = tag ?: T::class.java.toString()
    return Kodi.createOrGet(tagToWrap.asTag()) {
        throwException<IllegalAccessException>("There is no tag `$tagToWrap` in dependency graph injected into IKodi instance [$instance]")
    }
}

/**
 * Lazy immutable property initializer wrapper for injection
 * Example: `val someValue by immutableInstance<ISomeValueClass>()`
 * It cannot be change
 *
 * @param tag - there is an optional tag that we pass to key into dependency graph
 */
inline fun <reified T : Any> IKodi.immutableInstance(tag: String? = null): IImmutableDelegate<T> = immutableGetter {
    instance<T>(tag)
}

/**
 * Lazy mutable property initializer wrapper for injection
 * Example: `var someValue by mutableInstance<ISomeValueClass>()`
 * It can be change `someValue = object : ISomeValueClass {}`
 *
 * @param tag - there is an optional tag that we pass to key into dependency graph
 */
inline fun <reified T : Any> IKodi.mutableInstance(tag: String? = null): IMutableDelegate<T> = mutableGetter {
    instance<T>(tag)
}

/**
 * Extension function to IKodi implementation to get reference to Generic<T>
 * @param clazz - [Class]::class.java
 */
@CanThrowException("There is no KodiHolder instance in dependency graph")
fun<T : Any> IKodi.instanceWith(clazz: Class<T>): T {
    val instance = this
    val tagToWrap = clazz.toString()
    return Kodi.createOrGet(tagToWrap.asTag()) {
        throwException<IllegalAccessException>("There is no tag `$tagToWrap` in dependency graph injected into IKodi instance [$instance]")
    }.collect(this)
}

/**
 * Extension function to IKodi implementation to get reference to Generic<T>
 * @param tag - String tag
 */
@CanThrowException("There is no KodiHolder instance in dependency graph")
fun<T : Any> IKodi.instanceWith(tag: String): T {
    val instance = this
    return Kodi.createOrGet(tag.asTag()) {
        throwException<IllegalAccessException>("There is no tag `$tag` in dependency graph injected into IKodi instance [${instance}]")
    }.collect(this)
}

/**
 * Collect instance from [KodiHolder]
 * @param kodiImpl - implemented instance of [IKodi]
 */
@Suppress("UNCHECKED_CAST")
@CanThrowException("Cannot cast instance from KodiHolder to T")
fun<T : Any> KodiHolder.collect(kodiImpl: IKodi): T {
    return when (this) {
        is KodiHolder.KodiSingle<*> -> get()
        is KodiHolder.KodiProvider<*> -> providerLiteral(kodiImpl)
        is KodiHolder.KodiConstant<*> -> constantValue
    }.run {
        (this as? T) ?: throwException<ClassCastException>("Cannot cast instance $this to T")
    }
}