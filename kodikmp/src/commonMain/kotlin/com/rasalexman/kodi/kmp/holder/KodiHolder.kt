package com.rasalexman.kodi.kmp.holder

import com.rasalexman.kodi.kmp.Kodi
import com.rasalexman.kodi.kmp.common.defaultScope
import com.rasalexman.kodi.kmp.core.IKodi
import com.rasalexman.kodi.kmp.core.InstanceInitializer
import com.rasalexman.kodi.kmp.extensions.or
import com.rasalexman.kodi.kmp.extensions.throwKodiException
import com.rasalexman.kodi.kmp.wrapper.KodiScopeWrapper
import com.rasalexman.kodi.kmp.wrapper.KodiTagWrapper

/**
 * Available classes for binding
 */
sealed class KodiHolder<T : Any> {

    /**
     * Getting value from holder
     */
    abstract fun get(kodiImpl: IKodi): T


    fun IKodi.holderValue(): T = get(this)

    /**
     * Local Holder scope [KodiScopeWrapper]
     */
    var scope: KodiScopeWrapper = defaultScope
        private set

    /**
     * Current Holder [KodiTagWrapper]
     */
    var tag: KodiTagWrapper = KodiTagWrapper.Companion.emptyTag()
        private set

    internal val storageKey: String
        get() = "${scope.asString()}_${tag.asString()}"

    /**
     * Add [KodiTagWrapper] to current Holder
     * And put it into dependency scope
     *
     * @param instanceTag - tag for instance binding
     */
    internal infix fun <T : Any> KodiHolder<T>.tagWith(instanceTag: KodiTagWrapper): KodiHolder<T> {
        if (instanceTag.isNotEmpty() && tag != instanceTag) {
            tag = instanceTag
            addToGraph()
        } else if (!instanceTag.isNotEmpty()) {
            removeFromGraph()
        } else {
            throwKodiException<IllegalStateException>(
                message = "You can't change tag `${tag.asString()}` on `$this`. Only set it to `KodiTagWrapper.emptyTag()`"
            )
        }
        return this
    }

    /**
     * Add Instance Tag to moduleScope
     *
     * @param scopeWrapper - [KodiScopeWrapper] to add instance tag
     * @return [KodiHolder]
     */
    internal infix fun <T : Any> KodiHolder<T>.scopeWith(scopeWrapper: KodiScopeWrapper): KodiHolder<T> {
        if (scopeWrapper.isNotEmpty() && scope != scopeWrapper) scope = scopeWrapper
        return this
    }

    /**
     * Clear current KodiHolder instance
     */
    open fun clear() {
        scope = defaultScope
        tag = KodiTagWrapper.emptyTag()
    }

    /**
     * Add current holder to instance storage
     */
    private fun addToGraph() {
        if (tag.isNotEmpty()) {
            Kodi.createOrGet(tag, scope, ::getCurrentHolder)
        }
    }

    /**
     * Remove current holder from instance storage
     */
    private fun removeFromGraph() {
        if (tag.isNotEmpty()) {
            Kodi.removeInstance(tag, scope)
        }
    }

    /**
     * Take current holder for delegation in graph
     */
    private fun getCurrentHolder() = this

    /**
     * Single Instance Holder withScope lazy initialization
     *
     * @param singleInstanceProvider - the single lazy immutable instance provider
     */
    data class KodiSingle<ReturnType : Any>(
        private var singleInstanceProvider: InstanceInitializer<ReturnType>?
    ) : KodiHolder<ReturnType>() {
        /**
         * Lazy initialized instance
         */
        private var singleInstance: ReturnType? = null

        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): ReturnType {
            return singleInstance.or {
                singleInstanceProvider?.invoke(Kodi).or {
                    throwKodiException<NoSuchElementException>(
                        message = "There is no instance provider or it's already null"
                    )
                }.also {
                    singleInstance = it
                }
            }
        }

        /**
         * Clear current Single Holder
         */
        override fun clear() {
            super.clear()
            singleInstance = null
            singleInstanceProvider = null
        }
    }

    /**
     * Provider Instance Holder withScope many execution
     *
     * @param providerLiteral - [InstanceInitializer] function
     */
    data class KodiProvider<ReturnType : Any>(
        private var providerLiteral: InstanceInitializer<ReturnType>?
    ) : KodiHolder<ReturnType>() {
        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): ReturnType = providerLiteral?.invoke(kodiImpl)
            ?: throwKodiException<NoSuchElementException>(
                message = "There is no instance provider or it's already null"
            )


        /**
         * Clear current provider from literal
         */
        override fun clear() {
            super.clear()
            providerLiteral = null
        }
    }

    /**
     * Constant value instance holder
     *
     * @param constantValue - value for initialization
     */
    data class KodiConstant<ReturnType : Any>(
        private var constantValue: ReturnType?
    ) : KodiHolder<ReturnType>() {
        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): ReturnType {
            return constantValue ?: throwKodiException<NoSuchElementException>(
                message = "There is no instance in [KodiConstant]"
            )
        }

        /**
         * Clear KodiConstant value
         */
        override fun clear() {
            super.clear()
            constantValue = null
        }
    }
}