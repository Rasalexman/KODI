package com.mincor.kodiexample

import com.mincor.kodi.core.*
import java.util.*
import kotlin.contracts.ExperimentalContracts

const val SOME_CONSTANT_TAG = "SOME_CONSTANT_TAG"

const val MY_PROVIDER_SCOPE_NAME = "MY_PROVIDER_SCOPE_NAME"
const val MY_SINGLE_SCOPE_NAME = "MY_SINGLE_SCOPE_NAME"

@ExperimentalContracts
fun main(args: Array<String>) {

    val kodiModule = kodiModule {
        bind<ISingleInterface>() with single { SingleClass(UUID.randomUUID().toString()) }
        bind<IProviderInterface>() with provider { ProviderClass(UUID.randomUUID().toString()) } at scope(MY_PROVIDER_SCOPE_NAME)
    } withScope MY_SINGLE_SCOPE_NAME.asScope()

    val isns = kodi {

        import(kodiModule)

        bind<String>(SOME_CONSTANT_TAG) with constant { "Hello" }
        bind<IInjectable>() with single { InjectableClass(instance(), instance()) } at MY_SINGLE_SCOPE_NAME.asScope()

        val singleInstance = instance<ISingleInterface>()
        singleInstance.printName()

        val providerLazyInstance by immutableInstance<IProviderInterface>()
        val injectableSinge by immutableInstance<IInjectable>()
        instance<IProviderInterface>().printName()
        instance<IProviderInterface>().printName()
        //instance<ISingleInterface>().printName()
        providerLazyInstance.printName()

        //println("-------> ${instance<String>("sre")}")

        val anotherSingleInstance = instance<ISingleInterface>()
        anotherSingleInstance.printName()

        injectableSinge.providerInstance.printName()
        injectableSinge.singleInstance.printName()

        //unbind<IProviderInterface>() from MY_PROVIDER_SCOPE_NAME.asScope()

        //holder<IInjectable>() at MY_PROVIDER_SCOPE_NAME.asScope()

        //unbindScope(MY_SINGLE_SCOPE_NAME.asScope())
    }
}

interface IPrintable {
    val id: String
    val name: String
    fun printName() {
        println("-----> Class name: ${this.name} withScope id: $id")
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

