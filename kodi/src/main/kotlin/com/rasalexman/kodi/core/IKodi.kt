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

package com.rasalexman.kodi.core

import com.rasalexman.kodi.delegates.IImmutableDelegate
import com.rasalexman.kodi.delegates.IMutableDelegate
import com.rasalexman.kodi.delegates.immutableGetter
import com.rasalexman.kodi.delegates.mutableGetter

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
    val receiver = this
    return (tag ?: "${T::class.java}").asTag().also {
        if (receiver is KodiModule) {
            receiver.moduleInstancesSet.add(it)
        }
    }
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
inline fun <reified T : Any, reified R : T> IKodi.bindType(tag: String? = null): KodiTagWrapper {
    return this.bind<R>(tag)
}

/**
 * Unbind instance by given tag or generic type
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.toString()`
 *
 * @param scope - optional parameter for removing instance from selected scope name
 *
 * @return [Boolean] - is instance removed
 */
inline fun <reified T : Any> IKodi.unbind(tag: String? = null, scope: String? = null): Boolean {
    val tagToWrapper = (tag ?: "${T::class.java}").asTag()
    val scopeToWrap = scope?.asScope() ?: defaultScope
    return Kodi.removeInstance(tagToWrapper, scopeToWrap) != null
}

/**
 * Get instance tag or generic class has been Scope name
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.toString()`
 *
 * @return [String] optional if it has scope return it name or null
 */
@Deprecated("Not currently in use and will be removed in Release 1.3.0")
inline fun <reified T : Any> IKodi.getScope(tag: String? = null): String? {
    val tagToWrap = (tag ?: "${T::class.java}").asTag()
    return if (Kodi.hasInstance(tagToWrap)) {
        null
    } else null
}

/**
 * Check if tag or generic class has been added to Scope
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.toString()`
 *
 * @return [Boolean] true if it has scope or false if it doesn't
 */
@Deprecated(message = "Not currently used and will be removed in Release version 1.3.0", replaceWith = ReplaceWith("Nothing to replace with"))
inline fun <reified T : Any> IKodi.hasScope(tag: String? = null): Boolean {
    return getScope<T>(tag) != null
}

/**
 * Check if tag or generic class has been added to any Kodi Module
 *
 * @return [Boolean]
 */
inline fun <reified T : Any> IKodi.hasModule(tag: String? = null): Boolean {
    val tagToWrap = (tag ?: "${T::class.java}").asTag()
    return Kodi.hasModuleByTag(tagToWrap)
}

/**
 * Check if tag or generic class has been added into Kodi dependency graph
 *
 * @return [Boolean]
 */
inline fun <reified T : Any> IKodi.isKodiInstance(tag: String? = null): Boolean {
    val tagToWrap = (tag ?: "${T::class.java}").asTag()
    return Kodi.hasInstance(tagToWrap)
}

/**
 * Bind only by tag Instance
 *
 * @param tag - required parameter for key in injection graph
 *
 * @receiver [IKodi]
 * @return [KodiTagWrapper]
 */
@CanThrowException("Parameter tag cannot be empty string")
fun IKodi.bindTag(tag: String): KodiTagWrapper {
    return tag.takeIf { it.isNotEmpty() }?.let {
        this.bind<Any>(it)
    } ?: throwKodiException<IllegalArgumentException>("TAG CANNOT BE EMPTY")
}

/**
 * Unbind only by tag Instance
 *
 * @param tag - required parameter for key in injection graph
 *
 * @param scope - optional parameter for scope in injection graph or [defaultScope]
 *
 * @receiver [IKodi]
 * @return [KodiTagWrapper]
 */
@CanThrowException("Parameter tag cannot be empty string")
fun IKodi.unbindTag(tag: String, scope: String? = null): Boolean {
    return tag.takeIf { it.isNotEmpty() }?.let {
        this.unbind<Any>(it, scope)
    } ?: throwKodiException<IllegalArgumentException>("TAG CANNOT BE EMPTY")
}

/**
 * Unbind moduleScope and all instances from dependency graph
 *
 * @param scopeTagWrapper - [KodiScopeWrapper] to remove
 */
@Deprecated("Use [IKodi.unbindScope(scopeName: String)] and will be removed in future releases", ReplaceWith("[unbindScope(scopeName:String)]"))
fun IKodi.unbindScope(scopeTagWrapper: KodiScopeWrapper): Boolean {
    return Kodi.removeAllScope(scopeTagWrapper)
}

/**
 * Unbind moduleScope and all instances from dependency graph
 *
 * @param scopeName - [String] scope name to remove
 */
fun IKodi.unbindScope(scopeName: String): Boolean {
    return Kodi.removeAllScope(scopeName.asScope())
}

/**
 * Unbind moduleScope and all instances from dependency graph
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
inline fun <reified T : Any> IKodi.instance(tag: String? = null, scope: String? = null): T {
    return holder<T>(tag, scope).get(this) as? T
            ?: throwKodiException<ClassCastException>("Cannot cast instance $this to Generic type T")
}

/**
 * Take current [KodiHolder] instance for injection or throw an Exception if it's does't exist
 *
 * @param tag - String instance tag (Optional)
 * @throws IllegalAccessException - if there is no tag in dependency graph it's crash
 */
@CanThrowException("There is no KodiHolder instance in dependency graph")
inline fun <reified T : Any> IKodi.holder(tag: String? = null, scope: String? = null): KodiHolder {
    val instance = this
    val tagToWrap = (tag ?: "${T::class.java}").asTag()
    val scopeToWrap = scope?.asScope() ?: defaultScope
    return Kodi.createOrGet(tagToWrap, scopeToWrap) {
        throwKodiException<IllegalAccessException>("There is no tag `$tagToWrap` in dependency graph with scope `$scopeToWrap` injected into IKodi instance [$instance]")
    }
}

/**
 * Bind singleton, only one instance will be stored and injected
 *
 * @param init - instance initializer [InstanceInitializer]
 *
 * @return [KodiHolder.KodiSingle] implementation instance
 */
inline fun <reified T : Any> IKodi.single(noinline init: InstanceInitializer<T>): KodiHolder {
    return createHolder<KodiHolder.KodiSingle<T>, T>(init)
}

/**
 * Bind the provider function
 *
 * @param init - instance initializer [InstanceInitializer]
 *
 * @return [KodiHolder.KodiProvider] implementation instance
 */
inline fun <reified T : Any> IKodi.provider(noinline init: InstanceInitializer<T>): KodiHolder {
    return createHolder<KodiHolder.KodiProvider<T>, T>(init)
}

/**
 * Bind initialized constant value like String, Int, etc... or Any
 *
 * @param init - instance initializer [InstanceInitializer]
 *
 * @return [KodiHolder.KodiConstant] implementation instance
 */
inline fun <reified T : Any> IKodi.constant(noinline init: InstanceInitializer<T>): KodiHolder {
    return createHolder<KodiHolder.KodiConstant<T>, T>(init)
}

/**
 * Lazy immutable property initializer wrapper for injection
 * Example: `val someValue by immutableInstance<ISomeValueClass>()`
 * It cannot be change
 *
 * @param tag - there is an optional tag that we pass to key into dependency graph
 * @param scope - there is a scope of current instance
 */
inline fun <reified T : Any> IKodi.immutableInstance(tag: String? = null, scope: String? = null): IImmutableDelegate<T> = immutableGetter {
    instance<T>(tag, scope)
}

/**
 * Lazy mutable property initializer wrapper for injection
 * Example: `var someValue by mutableInstance<ISomeValueClass>()`
 * It can be change `someValue = object : ISomeValueClass {}`
 *
 * @param tag - there is an optional tag that we pass to key into dependency graph
 * @param scope - there is a scope of current instance
 */
inline fun <reified T : Any> IKodi.mutableInstance(tag: String? = null, scope: String? = null): IMutableDelegate<T> = mutableGetter {
    instance<T>(tag, scope)
}

/**
 * * Extension function to IKodi implementation to get reference to Generic<T> or Dynamically add instance to dependency graph
 * If there is no instance by given Class<T> and `initKodiHolder == null` there is an Exception will be throw
 *
 * @param clazz - [Class]::class.java
 * @param initKodiHolder - optional [KodiHolder]
 *
 * @throws IllegalAccessException - if there is no tag in dependency graph
 */
@CanThrowException("There is no KodiHolder instance in dependency graph")
@Deprecated("Not currently needed functionality and will be removed in Release version 1.3.0")
fun <T : Any> IKodi.instanceWith(clazz: Class<T>, initKodiHolder: KodiHolder? = null): T {
    val tagToWrap = clazz.toString().asTag()
    return getInstanceByWrapperOrCreateDynamically(tagToWrap, defaultScope, initKodiHolder)
}

/**
 * Extension function to IKodi implementation to get reference to Generic<T> or Dynamically add instance to dependency graph
 * If there is no instance by given tag and `initKodiHolder == null` there is an Exception will be throw
 *
 * @param tag - String tag
 * @param initKodiHolder - optional [KodiHolder]
 *
 * @throws IllegalAccessException - if there is no tag in dependency graph
 */
@CanThrowException("There is no KodiHolder instance in dependency graph")
@Deprecated("Not currently needed functionality and will be removed in Release version 1.3.0")
fun <T : Any> IKodi.instanceWith(tag: String, initKodiHolder: KodiHolder? = null): T {
    val tagToWrap = tag.asTag()
    return getInstanceByWrapperOrCreateDynamically(tagToWrap, defaultScope, initKodiHolder)
}

/**
 * Private get or create function with Exception
 *
 * @param kodiTagWrapper - [KodiTagWrapper]
 * @param initKodiHolder - optional [KodiHolder]
 *
 * @throws IllegalAccessException - if there is no tag in dependency graph
 */
@Suppress("UNCHECKED_CAST")
@CanThrowException("There is no KodiHolder instance in dependency graph")
@Deprecated("Not currently needed functionality and will be removed in Release version 1.3.0")
private fun <T : Any> IKodi.getInstanceByWrapperOrCreateDynamically(kodiTagWrapper: KodiTagWrapper, scopeScopeWrapper: KodiScopeWrapper = defaultScope, initKodiHolder: KodiHolder? = null): T {
    val instance = this
    return Kodi.createOrGet(kodiTagWrapper, scopeScopeWrapper) {
        initKodiHolder
                ?: throwKodiException<IllegalAccessException>("There is no tag `$kodiTagWrapper` in dependency graph injected into IKodi instance [${instance}]")
    }.get(this) as? T
            ?: throwKodiException<ClassCastException>("Cannot cast instance of $initKodiHolder to Generic type T")
}