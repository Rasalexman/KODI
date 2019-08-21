package com.mincor.kodiexample

import com.mincor.kodi.core.*
import java.util.*

fun main(args: Array<String>) {
    val isns = initKODI {

        bind<ISingleInterface>() with single { SingleClass(UUID.randomUUID().toString()) }
        bind<IProviderInterface>() with provider { ProviderClass(UUID.randomUUID().toString()) }

        //bind<String>("sre") with single { "Hello" }

        bind<IInjectable>() with single { InjectableClass(instance(), instance()) }

        //val singleInstance = instance<ISingleInterface>()
        //singleInstance.printName()

        val providerLazyInstance by instanceLazy<IProviderInterface>()
        instance<IProviderInterface>().printName()
        instance<IProviderInterface>().printName()
        //instance<ISingleInterface>().printName()
        providerLazyInstance.printName()

        //println("-------> ${instance<String>("sre")}")

        val injectableSinge = instance<IInjectable>()
        injectableSinge.providerInstance.printName()
        injectableSinge.singleInstance.printName()
    }
}

interface IPrintable {
    val id: String
    val name: String
    fun printName() {
        println("-----> Class name: ${this.name} with id: $id")
    }
}

interface ISingleInterface : IPrintable
data class SingleClass(override val id: String, override val name: String = "Single") : ISingleInterface

interface IProviderInterface : IPrintable, Cloneable
data class ProviderClass(override val id: String, override val name: String = "Provider") : IProviderInterface

interface IInjectable {
    val singleInstance: ISingleInterface
    val providerInstance: IProviderInterface
}

data class InjectableClass(
        override val providerInstance: IProviderInterface,
        override val singleInstance: ISingleInterface
) : IInjectable