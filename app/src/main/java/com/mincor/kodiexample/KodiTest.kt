package com.mincor.kodiexample

import com.mincor.kodiexample.domain.usecases.base.IUseCase
import com.rasalexman.kodi.core.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

//const val SOME_CONSTANT_TAG = "SOME_CONSTANT_TAG"

const val MY_PROVIDER_SCOPE_NAME = "MY_PROVIDER_SCOPE_NAME"
const val MY_SINGLE_SCOPE_NAME = "MY_SINGLE_SCOPE_NAME"
//const val MY_ANOTHER_SCOPE_NAME = "MY_ANOTHER_SCOPE_NAME"
const val MY_EXCLUSIV_SCOPE_NAME = "MY_EXCLUSIV_SCOPE_NAME"

const val TAG = "----->"

const val FIRST_SCOPE = "FIRST_SCOPE"
const val SECOND_SCOPE = "SECOND_SCOPE"

@ExperimentalCoroutinesApi
fun main() {

    val kodiModule = kodiModule {
        bind<ISingleInterface>() with single { SingleClass(UUID.randomUUID().toString()) }
        bind<IProviderInterface>() at MY_PROVIDER_SCOPE_NAME with provider { ProviderClass(UUID.randomUUID().toString()) }

        import(com.kodi.generated.modules.kodi.kodiModule)
        bindType<ISingleInterface, AnotherSingleClass>() with single { AnotherSingleClass(UUID.randomUUID().toString()) }
        bindType<ISingleInterface, OneMoreSingleClass>() with single { OneMoreSingleClass(UUID.randomUUID().toString()) }
    } withScope MY_SINGLE_SCOPE_NAME

    val anotherModule = kodiModule {
        bind<ISingleInterface>() with single { AnotherSingleClass(UUID.randomUUID().toString()) }
        bind<ISingleInterface>() at MY_EXCLUSIV_SCOPE_NAME with single { SingleClass(UUID.randomUUID().toString()) }

        bindTag("Hello") with constant { "Hello" }

        bind<IProviderInterface>() with provider { ProviderClass(UUID.randomUUID().toString()) }
        // with parameters
        bind<IProviderWithParamsInterface>() with provider {
            ProviderClass(UUID.randomUUID().toString())
        }
        bind<IClass>() at SECOND_SCOPE with single { SecondClass(instance(), instance()) }
        bind<IClass>() at FIRST_SCOPE with single { FirstClass(instance()) }
    } //withScope MY_ANOTHER_SCOPE_NAME

    kodi {

        import(kodiModule)
        import(anotherModule)
        //
        //val defaultParamsItem: IProviderWithParamsInterface by immutableInstance("Random")
        //defaultParamsItem.printName()
        
        addBindingListener<ISingleInterface>(scope = MY_SINGLE_SCOPE_NAME) {
            println("$TAG addBindingListener1 value = ${it.get(this).printName()}")
        }
        addBindingListener<ISingleInterface>(scope = MY_SINGLE_SCOPE_NAME) {
            println("$TAG addBindingListener2 value = ${it.get(this).printName()}")
        }
        addUnbindingListener<ISingleInterface>  {
            println("$TAG addUnbindingListener value = ${it.tag.asString()} ")
        }

        val instanceListener: KodiHolderHandler<IProviderWithParamsInterface> = {
            println("$TAG addInstanceListener value = ${it.tag.asString()}")
        }
        addInstanceListener(listener = instanceListener)

        //val scopedInstance: ISingleInterface = instance(scope = MY_SINGLE_SCOPE_NAME)
        //scopedInstance.printName()

        val providerFromScope: IProviderInterface = instance(scope = MY_PROVIDER_SCOPE_NAME)
        providerFromScope.printName()
        return

        val instanceWithParams: IProviderWithParamsInterface = instance("Hello Params")
        instanceWithParams.printName()
        val instanceWithDefaultParams: IProviderWithParamsInterface = instance()
        instanceWithDefaultParams.printName()

//        val firstModuleInstance: ISingleInterface = instance(scope = MY_ANOTHER_SCOPE_NAME)
        val secondModuleInstance: ISingleInterface = instance()
        secondModuleInstance.printName()
        val exclusivModuleInstance: ISingleInterface = instance(scope = MY_EXCLUSIV_SCOPE_NAME)
        exclusivModuleInstance.printName()

//        val myProvider: IProviderInterface = instance()
//        val myProviderByScope: IProviderInterface = instance(scope = MY_PROVIDER_SCOPE_NAME)
//
//        val firstInstance: IClass = instance(scope = FIRST_SCOPE)
//        val secondInstance: IClass = instance(scope = SECOND_SCOPE)

        /*val coroutineManager = CoroutinesManager()
        coroutineManager.launch {
            val result = firstInstance("true", "true")
            log { "firstInstance id = $result" }
        }

        coroutineManager.launch {
            val result = secondInstance("true", "true")
            log { "secondInstance id = $result" }
        }*/

        removeInstanceListener<IProviderWithParamsInterface>(listener = instanceListener)
        unbind<ISingleInterface>()
        //val removedInterface: ISingleInterface = instance(scope = MY_ANOTHER_SCOPE_NAME)

        //log { "Is instance equals = ${firstModuleInstance == secondModuleInstance}" }
    }

    /*kodi {
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
    }*/
}

interface IPrintable {
    fun printName()
}

interface IDefaultModel : IPrintable {
    val id: String
    val name: String

    override fun printName() {
        println("$TAG ${this.name} | with id: $id")
    }
}

interface ISingleInterface : IDefaultModel
data class SingleClass(override val id: String, override val name: String = "Single") : ISingleInterface
data class AnotherSingleClass(override val id: String, override val name: String = "Another") : ISingleInterface
data class OneMoreSingleClass(override val id: String, override val name: String = "OneMore") : ISingleInterface
data class DynamicSingleClass(override val id: String, override val name: String = "Dynamic") : ISingleInterface

interface IProviderInterface : IDefaultModel, Cloneable
interface IProviderWithParamsInterface : IProviderInterface
data class ProviderClass(override val id: String, override val name: String = "Provider") : IProviderWithParamsInterface {
    override fun printName() {
        println("$TAG ProviderClass with start params = $id")
    }
}

open class FirstClass(
        private val anotherSingleClass: IProviderInterface
) : IClass, ISingleInterface {
    override val id: String = "hello im first"
    override val name: String = "ISingleInterface"
    override suspend fun invoke(first: String, second: String): Boolean {
        return first == second
    }
}

class SecondClass(
        anotherSingleClass: IProviderInterface,
        private val sinter: ISingleInterface
) : FirstClass(anotherSingleClass) {
    override val id: String = "hello im second"

    override suspend fun invoke(first: String, second: String): Boolean {
        return if(first == second) super.invoke(first, second) else false
    }
}

interface IInjectable {
    val singleInstance: ISingleInterface
    val providerInstance: IProviderInterface
}

data class InjectableClass(
        override val providerInstance: IProviderInterface,
        override val singleInstance: ISingleInterface
) : IInjectable

interface IClass : IUseCase.DoubleInOut<String, String, Boolean>


