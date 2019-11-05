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
 * Typealias for simplification
 */
typealias InstanceInitializer<T> = IKodi.() -> T

/**
 * Available classes for binding
 */
sealed class KodiHolder {

    /**
     * Local Holder scope [KodiScopeWrapper]
     */
    private var scope: KodiScopeWrapper = emptyScope()

    /**
     * Current Holder [KodiTagWrapper]
     */
    private var tag: KodiTagWrapper = emptyTag()

    /**
     * Add Instance Tag to moduleScope
     * Or remove it if input [KodiScopeWrapper] is [emptyScope]
     *
     * @param scopeWrapper - [KodiScopeWrapper] to add instance tag
     */
    infix fun at(scopeWrapper: KodiScopeWrapper) {
        removeFromScope()
        scope = scopeWrapper
        if (scopeWrapper.isNotEmpty()) {
            addToScope()
        }
    }

    /**
     * Add [KodiTagWrapper] to current Holder
     * And put it into dependency scope
     *
     * @param instanceTag - tag for instance binding
     */
    infix fun tag(instanceTag: KodiTagWrapper): KodiHolder {
        if (instanceTag.isNotEmpty() && !this.tag.isNotEmpty()) {
            this.tag = instanceTag
            addToGraph()
            addToScope()
        } else if (!instanceTag.isNotEmpty()) {
            removeFromGraph()
            removeFromScope()
        } else {
            throwException<IllegalStateException>("You can't change tag `$tag` on `$this`. Only set it to emptyTag()")
        }
        return this
    }

    /**
     * Add current holder to instance storage
     */
    private fun addToGraph() {
        if (tag.isNotEmpty()) {
            Kodi.createOrGet(tag) { this }
        }
    }

    /**
     * Remove current holder from instance storage
     */
    private fun removeFromGraph() {
        if (tag.isNotEmpty()) {
            Kodi.removeInstance(tag)
        }
    }

    /**
     * Remove from existing `scope` when new is added
     */
    private fun removeFromScope() {
        if (scope.isNotEmpty() && tag.isNotEmpty()) {
            Kodi.removeFromScope(scope to tag)
        }
    }

    /**
     * Add current holder to scope
     */
    private fun addToScope() {
        if (scope.isNotEmpty() && tag.isNotEmpty()) {
            Kodi.addToScope(scope to tag)
        }
    }

    /**
     * Single Instance Holder withScope lazy initialization
     */
    data class KodiSingle<T : Any>(
            private val singleInstanceProvider: InstanceInitializer<T>
    ) : KodiHolder() {
        /**
         * Lazy initialized instance
         */
        private var singleInstance: T? = null

        /**
         * Get <T> Single Value
         */
        fun get(): T = singleInstance ?: Kodi.singleInstanceProvider().apply {
            singleInstance = this
        }
    }

    /**
     * Provider Instance Holder withScope many execution
     *
     * @param providerLiteral - [InstanceInitializer] function
     */
    data class KodiProvider<T : Any>(val providerLiteral: InstanceInitializer<T>) : KodiHolder()

    /**
     * Constant value instance holder
     *
     * @param constantValue - value for initialization
     */
    data class KodiConstant<T : Any>(val constantValue: T) : KodiHolder()
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
 * Create [KodiHolder] with given [InstanceInitializer]
 * It's also apply scope [KodiScopeWrapper] from [IKodiModule]
 *
 * @param init - noinline [InstanceInitializer]
 *
 * @return [KodiHolder] implementation instance
 */
inline fun <reified R : KodiHolder, reified T : Any> IKodi.createHolder(noinline init: InstanceInitializer<T>): KodiHolder {
    return when (R::class) {
        KodiHolder.KodiSingle::class -> KodiHolder.KodiSingle(init)
        KodiHolder.KodiProvider::class -> KodiHolder.KodiProvider(init)
        KodiHolder.KodiConstant::class -> KodiHolder.KodiConstant(init())
        else -> throw ClassCastException("There is no type holder like ${T::class}")
    }.applyIf(this is IKodiModule) { holder ->
        holder at (this as IKodiModule).scope
    }
}