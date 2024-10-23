package com.rasalexman.kodi.kmp.wrapper

import kotlin.jvm.JvmInline


/**
 * Inline instanceTag wrapper for storing injectable class
 *
 * @param instanceTag - String tag for instance `key` storage
 * @param originalTag - String for instance generic name
 */
//@JvmInline
data class KodiTagWrapper(
    private val instanceTag: KodiInstanceTagWrapper,
    private val originalTag: KodiOriginalTagWrapper
) {

    /**
     * Check instanceTag is not empty
     *
     * @return [Boolean]
     */
    fun isNotEmpty(): Boolean {
        return instanceTag.isNotEmpty()
    }

    /**
     * Return a [String] representation of Wrapper
     */
    fun asString(): String = this.instanceTag.asString()

    /**
     * Return a [String] representation of Original Generic Class Name
     */
    fun asOriginal(): String = this.originalTag.asString()

    override fun toString(): String {
        return asString()
    }

    companion object {
        /**
         * Empty instance tag holder.
         * This means that [KodiHolder] instance is no in dependency graph
         */
        fun emptyTag(): KodiTagWrapper {
            return KodiTagWrapper(KodiInstanceTagWrapper(""), KodiOriginalTagWrapper(""))
        }
    }
}


/**
 * Scope wrapper class
 *
 * @param scopeTag - String tag for moduleScope `key` storage
 */
@JvmInline
value class KodiScopeWrapper(private val scopeTag: String) {
    /**
     * Is Wrapper [scopeTag] empty
     *
     * @return [Boolean]
     */
    fun isNotEmpty(): Boolean = this.scopeTag.isNotEmpty()

    /**
     * Return a [String] representation of Wrapper
     */
    fun asString(): String = this.scopeTag
}

@JvmInline
value class KodiInstanceTagWrapper(private val value: String) {
    fun isNotEmpty(): Boolean = value.isNotEmpty()
    fun asString(): String = this.value
}

@JvmInline
value class KodiOriginalTagWrapper(private val value: String) {
    fun isNotEmpty(): Boolean = value.isNotEmpty()
    fun asString(): String = this.value
}
