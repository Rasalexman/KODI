package com.rasalexman.kodiannotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class KodiSingle(val bindTo: KClass<out Any>, val tag: String = "")

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KodiProvider

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KodiConstant
