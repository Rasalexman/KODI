package com.rasalexman.kodiannotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class BindProvider(
        val toClass: KClass<out Any>,
        val toTag: String = "",
        val atScope: String = "",
        val toModule: String = ""
)