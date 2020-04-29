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
 *  Typealias for simplification with params
 */
internal typealias InstanceInitializerWithParam<T, R> = IKodi.(R?) -> T

/**
 * Available classes for binding
 */
sealed class KodiHolder {

    /**
     * Getting value from holder
     */
    abstract fun get(kodiImpl: IKodi): Any

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
    internal infix fun KodiHolder.tagWith(instanceTag: KodiTagWrapper) {
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
    internal infix fun KodiHolder.scopeWith(scopeWrapper: KodiScopeWrapper): KodiHolder {
        scope = scopeWrapper
        return this
    }

    /**
     * Clear current KodiHolder instance
     */
    open fun clear() {
        scope = defaultScope
        tag = emptyTag()
    }

    /**
     * Add current holder to instance storage
     */
    private fun addToGraph() {
        if (tag.isNotEmpty()) {
            Kodi.createOrGet(tag, defaultScope, ::getCurrentHolder)
            if (scope.isNotEmpty() && scope != defaultScope) {
                Kodi.createOrGet(tag, scope, ::getCurrentHolder)
            }
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
    data class KodiSingle<T : Any>(
            private var singleInstanceProvider: InstanceInitializer<T>?
    ) : KodiHolder() {
        /**
         * Lazy initialized instance
         */
        private var singleInstance: T? = null

        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): T = singleInstance
                ?: singleInstanceProvider?.invoke(Kodi)?.apply {
                    singleInstance = this
                }
                ?: throwKodiException<NoSuchElementException>("There is no instance provider or it's already null")

        /**
         * Clear current Single Holder
         */
        override fun clear() {
            singleInstance = null
            singleInstanceProvider = null
            super.clear()
        }
    }

    /**
     * Provider Instance Holder withScope many execution
     *
     * @param providerLiteral - [InstanceInitializer] function
     */
    data class KodiProvider<T : Any>(private var providerLiteral: InstanceInitializer<T>?) : KodiHolder() {
        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): T {
            return providerLiteral?.invoke(kodiImpl)
                    ?: throwKodiException<NoSuchElementException>("There is no instance provider or it's already null")
        }

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
     * @param providerLiteral - [InstanceInitializer] function
     */
    data class KodiProviderWithParam<T : Any, R : Any?>(private var providerLiteral: InstanceInitializerWithParam<T, R>?) : KodiHolder() {
        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): T {
            return providerLiteral?.invoke(kodiImpl, null)
                    ?: throwKodiException<NoSuchElementException>("There is no instance provider or it's already null")
        }

        /**
         * Get value with params not implemented yet
         */
        fun getWithParam(kodiImpl: IKodi, param: R) = providerLiteral?.invoke(kodiImpl, param)
    }

    /**
     * Constant value instance holder
     *
     * @param constantValue - value for initialization
     */
    data class KodiConstant<T : Any>(private var constantValue: T?) : KodiHolder() {
        /**
         * Get holder value
         * @param kodiImpl - implemented [IKodi] instance
         */
        override fun get(kodiImpl: IKodi): T {
            return constantValue
                    ?: throwKodiException<NoSuchElementException>("There is no instance in [KodiConstant]")
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

/**
 * Add Instance Tag to moduleScope
 * Or remove it if input [KodiScopeWrapper] is [emptyScope]
 *
 * @param scopeWrapper - [KodiScopeWrapper] to add instance tag
 */
infix fun KodiHolder.at(scopeWrapper: KodiScopeWrapper): KodiHolder {
    return this.scopeWith(scopeWrapper)
}

/**
 * Add [KodiTagWrapper] to current Holder
 * And put it into dependency scope
 *
 * @param instanceTag - tag for instance binding
 */
infix fun KodiHolder.tag(instanceTag: KodiTagWrapper) {
    this.tagWith(instanceTag)
}


/**
 * Create [KodiHolder] with given [InstanceInitializer]
 * It's also apply scope [KodiScopeWrapper] from [IKodiModule]
 *
 * @param init - noinline [InstanceInitializer]
 *
 * @return [KodiHolder] implementation instance
 */
@CanThrowException("If there is no typed initializer passed throw an exception")
inline fun <reified R : KodiHolder, reified T : Any> IKodi.createHolder(noinline init: InstanceInitializer<T>): R {
    return when (R::class.java) {
        KodiHolder.KodiSingle::class.java -> KodiHolder.KodiSingle(init)
        KodiHolder.KodiProvider::class.java -> KodiHolder.KodiProvider(init)
        KodiHolder.KodiConstant::class.java -> KodiHolder.KodiConstant(init())
        else -> throwKodiException<ClassCastException>("There is no type holder like ${T::class.java}")
    }.holderAs(this as? IKodiModule) { module -> this at module.scope } as R
}