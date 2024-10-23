package com.rasalexman.kodi.kmp.wrapper

import kotlin.jvm.JvmInline


/**
 * Inline instanceTag wrapper for storing injectable class
 *
 * @param instanceTag - String tag for instance `key` storage
 * @param originalTag - String for instance generic name
 */
data class KodiTagWrapper(
    private val instanceTag: KodiKeyWrapper,
    private val originalTag: KodiKeyWrapper,
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
         * This means that [com.rasalexman.kodi.kmp.holder.KodiHolder] instance is no in dependency graph
         */
        fun emptyTag(): KodiTagWrapper {
            return KodiTagWrapper(originalTag = KodiKeyWrapper(""), instanceTag = KodiKeyWrapper(""))
        }
    }
}


/**
 * Scope wrapper class
 *
 * @param key - String tag for moduleScope `key` storage
 */
@JvmInline
value class KodiKeyWrapper(private val key: String) {
    /**
     * Is Wrapper [key] empty
     *
     * @return [Boolean]
     */
    fun isNotEmpty(): Boolean = this.key.isNotEmpty()

    /**
     * Return a [String] representation of Wrapper
     */
    fun asString(): String = this.key
}
