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

/**
 * Typealias for simplification
 */
internal typealias InstanceInitializer<T> = IKodi.() -> T

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
     * Local Holder scope [KodiKeyWrapper]
     */
    var scope: KodiKeyWrapper = defaultScope
        private set

    /**
     * Current Holder [KodiTagWrapper]
     */
    var tag: KodiTagWrapper = KodiTagWrapper.emptyTag()
        private set

    internal val storageKey: String
        get() = scope toKeyBy tag

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
     * @param scopeWrapper - [KodiKeyWrapper] to add instance tag
     * @return [KodiHolder]
     */
    internal infix fun <T : Any> KodiHolder<T>.scopeWith(scopeWrapper: KodiKeyWrapper): KodiHolder<T> {
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