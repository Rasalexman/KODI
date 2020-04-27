package com.rasalexman.kodigen

import javax.lang.model.element.Element

data class KodiBindData(
        val element: Element,
        val toModule: String,
        val toPack: String,
        val toClass: String,
        val instanceType: String,
        val scope: String,
        val tag: String
)