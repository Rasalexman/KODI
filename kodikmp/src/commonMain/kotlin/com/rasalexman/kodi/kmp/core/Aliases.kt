package com.rasalexman.kodi.kmp.core

/**
 * Typealias for simplification
 */
internal typealias InstanceInitializer<T> = IKodi.() -> T

/**
 * Lambda wrapper withScope generic return Type
 */
internal typealias LambdaWithReturn<T> = () -> T

/**
 * Module Initializer
 */
internal typealias ModuleInitializer = InstanceInitializer<Unit>