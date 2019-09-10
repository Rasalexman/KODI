package com.mincor.kodiexample

import com.mincor.kodi.core.*
import java.util.*

const val SOME_CONSTANT_TAG = "SOME_CONSTANT_TAG"

const val MY_PROVIDER_SCOPE_NAME = "MY_PROVIDER_SCOPE_NAME"
const val MY_SINGLE_SCOPE_NAME = "MY_SINGLE_SCOPE_NAME"

fun main(args: Array<String>) {

    val kodiModule = kodiModule {
        bind<ISingleInterface>() with single { SingleClass(UUID.randomUUID().toString()) }
        bind<IProviderInterface>() with provider { ProviderClass(UUID.randomUUID().toString()) } at scope(MY_PROVIDER_SCOPE_NAME)
    } withScope MY_SINGLE_SCOPE_NAME.asScope()

    kodi {
        // Import module
        import(kodiModule)
        // bind constant value
        bind<String>(SOME_CONSTANT_TAG) with constant { "Hello" }
        // bind singleton value with lazy reciever properties
        bind<IInjectable>() with single { InjectableClass(instance(), instance()) } at MY_SINGLE_SCOPE_NAME.asScope()
        // get value
        val singleInstance = instance<ISingleInterface>()
        singleInstance.printName()
        // immutable variable
        val providerImmutableLazyInstance by immutableInstance<IProviderInterface>()
        // mutable variable
        var providerMutableLazyInstance by mutableInstance<IProviderInterface>()
        val injectableSinge = instance<IInjectable>()
        // another instance call
        instance<IProviderInterface>().printName()
        // call immutable variable
        providerImmutableLazyInstance.printName()
        // call mutable variable
        providerMutableLazyInstance.printName()
        providerMutableLazyInstance = ProviderClass("Another_id", "another name")
        //call singleton class as lazy
        injectableSinge.providerInstance.printName()
        injectableSinge.singleInstance.printName()
        // call of mutable instance
        providerMutableLazyInstance.printName()

        // unbind instance
        unbind<IProviderInterface>()

        //change instance scope
        holder<IInjectable>() at MY_PROVIDER_SCOPE_NAME.asScope()

        // unbind all scope of instances and removed it from dependency graph
        unbindScope(MY_SINGLE_SCOPE_NAME.asScope())
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

