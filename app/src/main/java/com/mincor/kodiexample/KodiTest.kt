package com.mincor.kodiexample

import com.mincor.kodi.core.*
import java.util.*

const val SOME_CONSTANT_TAG = "SOME_CONSTANT_TAG"

const val MY_PROVIDER_SCOPE_NAME = "MY_PROVIDER_SCOPE_NAME"
const val MY_SINGLE_SCOPE_NAME = "MY_SINGLE_SCOPE_NAME"

const val TAG = "----->"

fun main(args: Array<String>) {

    val kodiModule = kodiModule {
        bind<ISingleInterface>() with single { SingleClass(UUID.randomUUID().toString()) }
        bind<IProviderInterface>() with provider { ProviderClass(UUID.randomUUID().toString()) } at MY_PROVIDER_SCOPE_NAME

        bindType<ISingleInterface, AnotherSingleClass>() with single { AnotherSingleClass(UUID.randomUUID().toString()) }
        bindType<ISingleInterface, OneMoreSingleClass>() with single { OneMoreSingleClass(UUID.randomUUID().toString()) }
    } withScope MY_SINGLE_SCOPE_NAME

    kodi {
        // Import module
        import(kodiModule)
        // bind constant value
        bind<String>(SOME_CONSTANT_TAG) with constant { "Hello" }
        // bind singleton value with lazy receiver properties
        bind<IInjectable>() with single { InjectableClass(instance(), instance()) } at MY_SINGLE_SCOPE_NAME
        // Multi type instance from inherits
        val anotherInstance: ISingleInterface = instance<AnotherSingleClass>()
        anotherInstance.printName()

        val anoonemoreInstance: ISingleInterface = instance<AnotherSingleClass>()
        anoonemoreInstance.printName()

        // Call Instance of the same interface but another implementation
        val oneMoreInstance: ISingleInterface = instance<OneMoreSingleClass>()
        oneMoreInstance.printName()

        // get value
        val singleInstance = instanceWith(ISingleInterface::class.java)
        singleInstance.printName()

        // get Dynamic Instance and put it into dependency graph
        val dynamicInstance = instanceWith(DynamicSingleClass::class.java, single { DynamicSingleClass(UUID.randomUUID().toString()) } at MY_SINGLE_SCOPE_NAME)
        dynamicInstance.printName()


        val randomInstance = object : ISingleInterface {
            override val id: String = UUID.randomUUID().toString()
            override val name: String = "RandomInstance"
        }.also {
            it.printName()
        }

        val randomDynamicInstance = instanceWith(randomInstance::class.java, provider { randomInstance })
        randomDynamicInstance.printName()

        // immutable variable
        val providerImmutableLazyInstance by immutableInstance<IProviderInterface>()
        providerImmutableLazyInstance.printName()

        // mutable variable
        var providerMutableLazyInstance by mutableInstance<IProviderInterface>()
        val injectableSinge = instance<IInjectable>()
        // another instance call
        instance<IProviderInterface>().printName()
        // call immutable variable
        providerImmutableLazyInstance.printName()
        // call mutable variable
        providerMutableLazyInstance.printName()
        // change instance on the fly
        providerMutableLazyInstance = ProviderClass("Another_id", "Provider another name")
        //call singleton class as lazy
        injectableSinge.providerInstance.printName()
        injectableSinge.singleInstance.printName()
        // call of mutable instance
        providerMutableLazyInstance.printName()

        // check for instance holder in module
        println("$TAG IProviderInterface in module = ${kodiModule.hasInstance<IProviderInterface>()} | hasScope = ${hasScope<IProviderInterface>()}")

        // unbind instance
        unbind<IProviderInterface>()

        println("$TAG IProviderInterface in module after unbind = ${kodiModule.hasInstance<IProviderInterface>()}")

        //change instance scope
        holder<IInjectable>() at MY_PROVIDER_SCOPE_NAME

        // unbind all scope of instances and removed it from dependency graph
        unbindScope(MY_SINGLE_SCOPE_NAME)

        // You can use this unbinding function
        kodiModule.remove()

        println("$TAG AnotherSingleClass in module after remove = ${kodiModule.hasInstance<AnotherSingleClass>()} | hasScope = ${hasScope<AnotherSingleClass>()}")
    }
}

interface IPrintable {
    val id: String
    val name: String
    fun printName() {
        println("$TAG ${this.name} | with id: $id")
    }
}

interface ISingleInterface : IPrintable
data class SingleClass(override val id: String, override val name: String = "Single") : ISingleInterface
data class AnotherSingleClass(override val id: String, override val name: String = "Another") : ISingleInterface
data class OneMoreSingleClass(override val id: String, override val name: String = "OneMore") : ISingleInterface
data class DynamicSingleClass(override val id: String, override val name: String = "Dynamic") : ISingleInterface

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

