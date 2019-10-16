package com.mincor.kodireflect.ext

sealed class Exceptions(message: String) : Exception(message) {

    class ProviderException(tag: String) : Exceptions("There is no provider for given tag '$tag'. You must set function for provider")
}