package com.mincor.kodireflect.ext

/**
 * Base Exception sealed class
 *
 * @param message - message for exception
 */
sealed class Exceptions(message: String) : Exception(message) {

    /**
     * No provier exception
     * @param tag - tag of provider
     */
    class ProviderException(tag: String) : Exceptions("There is no provider for given tag '$tag'. You must set function for provider")
}