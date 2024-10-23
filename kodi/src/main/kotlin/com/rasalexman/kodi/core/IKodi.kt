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

import com.rasalexman.kodi.core.KodiHolder.*
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
    val message: String = "Check that the 'tag' is added to the dependency graph, otherwise it will fall with RuntimeException"
)

private const val TAG_EMPTY_ERROR = "Parameter 'tag' cannot be empty string"
internal const val SCOPE_EMPTY_ERROR = "Parameter 'scopeName' cannot be empty string"
private const val HOLDER_NULL_ERROR = "There is no 'KodiHolder' instance in dependency graph"
private const val INITIALIZER_NULL_ERROR = "There is no typed initializer passed throw an exception"

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
inline fun <reified ReturnType : Any> kodi(block: IKodi.() -> ReturnType): ReturnType {
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
inline fun <reified BindType : Any> IKodi.bind(
    tag: String? = null
): KodiTagWrapper {
    val receiver = this
    return tag.asTag<BindType>().also {
        if (receiver is IKodiModule) {
            receiver.moduleInstancesSet.add(it)
        }
    }
}

/**
 * Bind Typed Instance to inherits type like <ISimpleInterface, BaseSimpleInterfaceImplementation>
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.qualifiedName`
 *
 * @receiver [IKodi]
 * @return [KodiTagWrapper]
 */
inline fun <reified ParentType : Any, reified ChildType : ParentType> IKodi.bindType(tag: String? = null): KodiTagWrapper {
    return this.bind<ChildType>(tag)
}

/**
 * Unbind instance by given tag or generic type
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.java.toString()`
 *
 * @param scope - optional parameter for removing instance from selected scope name
 *
 * @return [Boolean] - is instance removed
 */
inline fun <reified UnbindType : Any> IKodi.unbind(
    tag: String? = null,
    scope: String? = null
): Boolean {
    val tagToWrapper = tag.asTag<UnbindType>()
    val scopeToWrap = scope.asScope()
    return Kodi.removeInstance(tagToWrapper, scopeToWrap)
}

/**
 * Check if tag or generic class has been added to any Kodi Module
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.java.toString()`
 *
 * @return [Boolean]
 */
inline fun <reified InstanceType : Any> hasModule(tag: String? = null): Boolean {
    val tagToWrap = tag.asTag<InstanceType>()
    return Kodi.hasModuleByTag(tagToWrap)
}

/**
 * Check if tag or generic class has been added into Kodi dependency graph
 *
 * @param tag - optional parameter for custom manipulating withScope instance tag
 * if there is no tag provided the generic class name will be used as `T::class.java.toString()`
 *
 * @return [Boolean]
 */
inline fun <reified InstanceType : Any> hasInstance(tag: String? = null): Boolean {
    val tagToWrap = tag.asTag<InstanceType>()
    return Kodi.hasInstance(tagToWrap)
}

/**
 * Bind Instance only by tag
 *
 * @param tag - required parameter for key in injection graph
 *
 * @receiver [IKodi]
 * @return [KodiTagWrapper]
 */
@CanThrowException(TAG_EMPTY_ERROR)
fun IKodi.bindTag(tag: String): KodiTagWrapper {
    return tag.takeIf { it.isNotEmpty() }?.let {
        this.bind<Any>(it)
    } ?: throwKodiException<IllegalArgumentException>(TAG_EMPTY_ERROR)
}

/**
 * Unbind Instance only by tag
 *
 * @param tag - required parameter for key in injection graph
 *
 * @param scope - optional parameter for scope in injection graph or [defaultScope]
 *
 * @receiver [IKodi]
 * @return [Boolean] is instance unbound by tag
 */
@CanThrowException(TAG_EMPTY_ERROR)
fun IKodi.unbindTag(tag: String, scope: String? = null): Boolean {
    return tag.takeIf { it.isNotEmpty() }?.let {
        this.unbind<Any>(tag, scope)
    } ?: throwKodiException<IllegalArgumentException>(TAG_EMPTY_ERROR)
}

/**
 * Unbind moduleScope and all instances from dependency graph
 *
 * @param scopeName - [String] scope name to remove
 *
 * @return [Boolean] flag - is instance unbound
 */
@CanThrowException(SCOPE_EMPTY_ERROR)
fun IKodi.unbindScope(scopeName: String): Boolean {
    if (scopeName.isEmpty()) throwKodiException<IllegalArgumentException>(SCOPE_EMPTY_ERROR)
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
 * @param scope - String of instance scope
 *
 * @return [InstanceType]
 * @throws ClassCastException - if there is no tag in dependency graph
 */
@CanThrowException(HOLDER_NULL_ERROR)
inline fun <reified InstanceType : Any> IKodi.instance(
    tag: String? = null,
    scope: String? = null
): InstanceType {
    val inst = holder<InstanceType>(tag = tag, scope = scope).get(kodiImpl = this)
    if(inst is InstanceType) {
        return inst
    }
    throwKodiException<ClassCastException>(
        message = "Cannot cast instance = $inst to Generic type ${genericName<InstanceType>()}"
    )
}

/**
 * Take current [KodiHolder] instance for injection or throw an Exception if it's doesn't exist
 *
 * @param tag - String instance tag (Optional)
 * @param scope - String of instance scope
 * @throws IllegalAccessException - if there is no tag in dependency graph it's crash
 */
@CanThrowException(HOLDER_NULL_ERROR)
inline fun <reified InstanceType : Any> IKodi.holder(
    tag: String? = null,
    scope: String? = null
): KodiHolder<out Any> {
    val instance = this
    val tagToWrap = tag.asTag<InstanceType>()
    val scopeToWrap = scope.asScope()
    return Kodi.getHolder(tag = tagToWrap, scope = scopeToWrap)
        ?: throwKodiException<IllegalAccessException>(
            message = "There is no tag `${tagToWrap.asString()}` in dependency graph with scope `${scopeToWrap.asString()}` injected into IKodi instance [$instance]"
        )
}

/**
 * Bind singleton, only one instance will be stored and injected
 *
 * @param init - instance initializer [InstanceInitializer]
 *
 * @return [KodiHolder.KodiSingle] implementation instance
 */
inline fun <reified SingleType : Any> IKodi.single(
    noinline init: InstanceInitializer<SingleType>
): KodiHolder<SingleType> {
    return createHolder<KodiSingle<SingleType>, SingleType>(init = init)
}

/**
 * Bind the provider function
 *
 * @param init - instance initializer [InstanceInitializer]
 *
 * @return [KodiHolder.KodiProvider] implementation instance
 */
inline fun <reified ProviderType : Any> IKodi.provider(
    noinline init: InstanceInitializer<ProviderType>
): KodiHolder<ProviderType> {
    return createHolder<KodiProvider<ProviderType>, ProviderType>(init = init)
}

/**
 * Bind initialized constant value like String, Int, etc... or Any
 *
 * @param init - instance initializer [InstanceInitializer]
 *
 * @return [KodiHolder.KodiConstant] implementation instance
 */
inline fun <reified ConstantType : Any> IKodi.constant(
    noinline init: InstanceInitializer<ConstantType>
): KodiHolder<ConstantType> {
    return createHolder<KodiConstant<ConstantType>, ConstantType>(init = init)
}


/**
 * Create [KodiHolder] with given [InstanceInitializer]
 * It's also apply scope [KodiKeyWrapper] from [IKodiModule]
 *
 * @param init - noinline [InstanceInitializer]
 *
 * @return [KodiHolder] implementation instance
 */
@CanThrowException(INITIALIZER_NULL_ERROR)
inline fun <reified HolderType : KodiHolder<ReturnType>, reified ReturnType : Any> IKodi.createHolder(
    noinline init: InstanceInitializer<ReturnType>
): HolderType {
    return when (HolderType::class) {
        KodiSingle::class -> KodiSingle(init)
        KodiProvider::class -> KodiProvider(init)
        KodiConstant::class -> KodiConstant(init())
        else -> throwKodiException<ClassCastException>("There is no type holder like \'${genericName<ReturnType>()}\'")
    }.withModuleScope(this) as HolderType
}

/**
 * Lazy immutable property initializer wrapper for injection
 * Example: `val someValue by immutableInstance<ISomeValueClass>()`
 * It cannot be change
 *
 * @param tag - there is an optional tag that we pass to key into dependency graph
 * @param scope - there is a scope of current instance
 */
inline fun <reified InstanceType : Any> immutableInstance(
    tag: String? = null,
    scope: String? = null
): IImmutableDelegate<InstanceType> = immutableGetter {
    instance(tag = tag, scope = scope)
}

/**
 * Lazy mutable property initializer wrapper for injection
 * Example: `var someValue by mutableInstance<ISomeValueClass>()`
 * It can be change `someValue = object : ISomeValueClass {}`
 *
 * @param tag - there is an optional tag that we pass to key into dependency graph
 * @param scope - there is a scope of current instance
 *
 * @return [IMutableDelegate] delegate with [InstanceType] holder
 */
inline fun <reified InstanceType : Any> mutableInstance(
    tag: String? = null,
    scope: String? = null
): IMutableDelegate<InstanceType> = mutableGetter {
    instance(tag = tag, scope = scope)
}