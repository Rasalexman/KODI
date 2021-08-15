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

package com.rasalexman.kodi.core

import java.lang.ref.WeakReference

/**
 * Typealias for simplification
 */
internal typealias InstanceInitializer<T> = IKodi.() -> T

/**
 *  Typealias for simplification with params
 */
internal typealias InstanceInitializerWithParam<R> = IKodi.(R?) -> Any

/**
 * Available classes for binding
 */
sealed class KodiHolder<T : Any> {

    /**
     * Getting value from holder
     */
    abstract fun get(kodiImpl: IKodi): T

    /**
     * Local Holder scope [KodiScopeWrapper]
     */
    var scope: KodiScopeWrapper = emptyScope()
        private set

    /**
     * Current Holder [KodiTagWrapper]
     */
    var tag: KodiTagWrapper = emptyTag()
        private set

    /**
     * Add [KodiTagWrapper] to current Holder
     * And put it into dependency scope
     *
     * @param instanceTag - tag for instance binding
     */
    internal infix fun <T : Any> KodiHolder<T>.tagWith(instanceTag: KodiTagWrapper) {
        if (instanceTag.isNotEmpty() && !tag.isNotEmpty()) {
            tag = instanceTag
            addToGraph()
        } else if (!instanceTag.isNotEmpty()) {
            removeFromGraph()
        } else {
            throwKodiException<IllegalStateException>("You can't change tag `$tag` on `$this`. Only set it to emptyTag()")
        }
    }

    /**
     * Add Instance Tag to moduleScope
     * Or remove it if input [KodiScopeWrapper] is [emptyScope]
     *
     * @param scopeWrapper - [KodiScopeWrapper] to add instance tag
     * @return [KodiHolder]
     */
    internal infix fun <T : Any> KodiHolder<T>.scopeWith(scopeWrapper: KodiScopeWrapper): KodiHolder<T> {
        scope = scopeWrapper
        return this
    }

    /**
     * Clear current KodiHolder instance
     */
    open fun clear() {
        notifyEventListeners(KodiEvent.UNBIND)
        scope = emptyScope()
        tag = emptyTag()
    }

    /**
     * Add current holder to instance storage
     */
    private fun addToGraph() {
        if (tag.isNotEmpty()) {
            val selectedScope = scope.takeIf {
                it.isNotEmpty() && it != defaultScope
            }.or { defaultScope }

            Kodi.createOrGet(tag, selectedScope, ::getCurrentHolder)
            notifyEventListeners(KodiEvent.BIND)
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
     * Notify all listener by event
     */
    protected fun notifyEventListeners(event: KodiEvent) {
        Kodi.notifyListeners(tag, scope, this, event)
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
        private var singleInstance: WeakReference<ReturnType>? = null

        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): ReturnType {
            return singleInstance?.get().or {
                singleInstanceProvider?.invoke(Kodi).or {
                    throwKodiException<NoSuchElementException>(
                        message = "There is no instance provider or it's already null"
                    )
                }.also {
                    singleInstance = WeakReference(it)
                    notifyEventListeners(KodiEvent.INSTANCE)
                }
            }
        }

        /**
         * Clear current Single Holder
         */
        override fun clear() {
            super.clear()
            singleInstance?.clear()
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
        override fun get(kodiImpl: IKodi): ReturnType = providerLiteral?.invoke(kodiImpl).or {
            throwKodiException<NoSuchElementException>("There is no instance provider or it's already null")
        }.also { notifyEventListeners(KodiEvent.INSTANCE) }

        /**
         * Clear current provider from literal
         */
        override fun clear() {
            super.clear()
            providerLiteral = null
        }
    }

    /**
     * Provider Instance Holder withScope many execution
     *
     * @param providerLiteral - [InstanceInitializerWithParam] function
     */
    data class KodiWithParamProvider<ParamType : Any>(
        private var providerLiteral: InstanceInitializerWithParam<ParamType>?,
        private var defaultParameter: ParamType? = null
    ) : KodiHolder<Any>() {
        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): Any {
            return providerLiteral?.invoke(kodiImpl, defaultParameter).or {
                throwKodiException<NoSuchElementException>(
                    message = "There is no instance provider with params or it's already null"
                )
            }.also { notifyEventListeners(KodiEvent.INSTANCE) }
        }

        /**
         * Get value with params not implemented yet
         */
        fun getWithParam(kodiImpl: IKodi, param: ParamType) =
            providerLiteral?.invoke(kodiImpl, param).or {
                throwKodiException<NoSuchElementException>(
                    message = "There is no instance provider with params or it's already null"
                )
            }.also { notifyEventListeners(KodiEvent.INSTANCE) }

        override fun clear() {
            super.clear()
            providerLiteral = null
            defaultParameter = null
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
            return constantValue.or {
                throwKodiException<NoSuchElementException>(
                    message = "There is no instance in [KodiConstant]"
                )
            }.also { notifyEventListeners(KodiEvent.INSTANCE) }
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