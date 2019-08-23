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
     * Single Instance Holder with lazy initialization
     */
    data class KodiSingle<T : Any>(
            private val singleInstanceProvider: InstanceInitializer<T>
    ) : KodiHolder() {
        /**
         * Lazy initialized instance
         */
        var singleInstance: T? = null
            get() = if (field == null) {
                field = Kodi.singleInstanceProvider()
                field
            } else field
    }

    /**
     * Provider Instance Holder with many execution
     */
    data class KodiProvider<T : Any>(val providerLiteral: InstanceInitializer<T>) : KodiHolder()

    /**
     * Constant value instance holder
     */
    data class KodiConstant<T : Any>(val constantValue: T) : KodiHolder()
}

/**
 * Bind singleton, only one instance will be stored and injected
 */
inline fun <reified T : Any> IKodi.single(noinline init: IKodi.() -> T): KodiHolder {
    return KodiHolder.KodiSingle(init)
}

/**
 * Bind the provider function
 */
inline fun <reified T : Any> IKodi.provider(noinline init: IKodi.() -> T): KodiHolder {
    return KodiHolder.KodiProvider(init)
}

/**
 * Bind initialized constant value like String, Int, etc... or Any
 */
inline fun <reified T : Any> IKodi.constant(noinline init: IKodi.() -> T): KodiHolder {
    return KodiHolder.KodiConstant(init())
}

