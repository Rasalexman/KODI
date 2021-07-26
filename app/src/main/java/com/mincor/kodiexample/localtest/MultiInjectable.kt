package com.mincor.kodiexample.localtest

import com.mincor.kodiexample.ISingleInterface
import com.rasalexman.kodi.annotations.BindProvider

@BindProvider(
    toClass = IMultiInjectable::class
)
class MultiInjectable : IMultiInjectable {

    override val id: String
        get() = hashCode().toString()

    override val name: String
        get() = "MultiInjectable"

    override fun showSuccess() {
        println("------> MultiInjectable::showSuccess id = $id")
    }

    override fun openScreen() {
        println("------> MultiInjectable::openScreen id = $id")
    }
}

interface IMultiInjectable : IMultiInjectable2 {
    fun showSuccess()
}

interface IMultiInjectable2 : ISingleInterface {
    fun openScreen()
}