package com.rasalexman.kodi.kmp

import com.rasalexman.kodi.kmp.common.CanThrowException
import com.rasalexman.kodi.kmp.common.HOLDER_NULL_ERROR
import com.rasalexman.kodi.kmp.common.INITIALIZER_NULL_ERROR
import com.rasalexman.kodi.kmp.common.SCOPE_EMPTY_ERROR
import com.rasalexman.kodi.kmp.common.TAG_EMPTY_ERROR
import com.rasalexman.kodi.kmp.core.IKodi
import com.rasalexman.kodi.kmp.core.IKodiModule
import com.rasalexman.kodi.kmp.core.InstanceInitializer
import com.rasalexman.kodi.kmp.delegates.IImmutableDelegate
import com.rasalexman.kodi.kmp.delegates.IMutableDelegate
import com.rasalexman.kodi.kmp.delegates.immutableGetter
import com.rasalexman.kodi.kmp.delegates.mutableGetter
import com.rasalexman.kodi.kmp.extensions.asScope
import com.rasalexman.kodi.kmp.extensions.asTag
import com.rasalexman.kodi.kmp.extensions.genericName
import com.rasalexman.kodi.kmp.extensions.throwKodiException
import com.rasalexman.kodi.kmp.extensions.withModuleScope
import com.rasalexman.kodi.kmp.holder.KodiHolder
import com.rasalexman.kodi.kmp.storage.KodiStorage
import com.rasalexman.kodi.kmp.wrapper.KodiTagWrapper

/**
 * Main Instance Holder object
 */
object Kodi : KodiStorage(), IKodi

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
 * @throws IllegalAccessException - if there is no tag in dependency graph
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
 * @throws IllegalStateException - if there is no tag in dependency graph it's crash
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
        ?: throwKodiException<IllegalStateException>(
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
    return createHolder<KodiHolder.KodiSingle<SingleType>, SingleType>(init = init)
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
    return createHolder<KodiHolder.KodiProvider<ProviderType>, ProviderType>(init = init)
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
    return createHolder<KodiHolder.KodiConstant<ConstantType>, ConstantType>(init = init)
}


/**
 * Create [KodiHolder] with given [InstanceInitializer]
 * It's also apply scope [KodiScopeWrapper] from [IKodiModule]
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
        KodiHolder.KodiSingle::class -> KodiHolder.KodiSingle(init)
        KodiHolder.KodiProvider::class -> KodiHolder.KodiProvider(init)
        KodiHolder.KodiConstant::class -> KodiHolder.KodiConstant(init())
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