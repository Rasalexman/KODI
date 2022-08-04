# KODI
KOtlin Dependency Injection (KODI) 

[ ![Kotlin 1.7.10](https://img.shields.io/badge/Kotlin-1.7.10-blue.svg)](http://kotlinlang.org) [![](https://jitpack.io/v/Rasalexman/KODI.svg)](https://jitpack.io/#Rasalexman/KODI) [![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/d298c3a2eb044d688f9a4b33bf352389)](https://www.codacy.com/manual/Rasalexman/KODI?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Rasalexman/KODI&amp;utm_campaign=Badge_Grade)

This is simple and useful dependency injection framework for work with your regular projects. It use standart Kotlin language construction like `literal function with recieve`, `infix function`, `hight-order function`, ets. to bind and inject dependencies into your objects. It has two packages:

1) Kodi library

It's a dependency injection library writen in pure Kotlin. This library has intuitive sintax like:

`bind<SomeClassInterface>() at "Any_String_Scope_Name" with singleton { SomeClassImplementation() }`

<b>No annotation processing, and reflection in core module.</b> 

You can `bind` three type of instances:
- `singleton`: only one instance  will be used. It's lazy instantiating
- `provider`: it's create instance every time it's called. Lazy also!
- `constant`: constant value by tag. It's create when it binded

In order to get a value or add a new one to the dependency graph, you need to call the `kodi {}` function from any part of your program. Recomendation to call it for binding at `MainApplication.kt` class. 
You can use `val myModule = kodiModule { bind<ISomeInstance> with ...}` for separate and organize independent component of your programm. Just call `kodi {import(myModule) }` to bind all dependencies from it.
Keywords:
- `bind`: bind string tag or generic type `with` given provided instance
- `bindType`: bind inherit type `with` given provided instance
- `at`: add instance at scope with string name
- `kodiModule { }`: instantiate module for dependency separation
- `withScope`: can used only with `kodiModule` for bind all instances at selected scope. If there is any scope binding inside this module, it will be used as main scope for this bindings

```kotlin
fun main(args: Array<String>) {
    // module with custom scope
    val kodiModule = kodiModule {
        bind<ISingleInterface>() with single { SingleClass(UUID.randomUUID().toString()) }
        // this will be use a `MY_PROVIDER_SCOPE_NAME` scope (since version 1.3.+)
        bind<IProviderInterface>() at MY_PROVIDER_SCOPE_NAME with provider { ProviderClass(UUID.randomUUID().toString()) }
	// since version 1.2.7
	bindType<ISingleInterface, AnotherSingleClass>() with single { AnotherSingleClass(UUID.randomUUID().toString()) }
        bindType<ISingleInterface, OneMoreSingleClass>() with single { OneMoreSingleClass(UUID.randomUUID().toString()) }
    } withScope MY_SINGLE_SCOPE_NAME.asScope()

    kodi {
        // Import module
        import(kodiModule)
        // bind constant value
        bind<String>(SOME_CONSTANT_TAG) with constant { "Hello" } at scope(CONSTANTS_SCOPE)
        // bind singleton value with lazy receiver properties
        bind<IInjectable>() with single { InjectableClass(instance(), instance()) } at MY_SINGLE_SCOPE_NAME.asScope()
        
	// Multi type instance from inherits (since 1.2.7)
        val anotherInstance: ISingleInterface = instance<AnotherSingleClass>()
        anotherInstance.printName()
        // Call Instance of the same interface but another implementation
        val oneMoreInstance: ISingleInterface = instance<OneMoreSingleClass>()
        oneMoreInstance.printName()
	
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
        providerMutableLazyInstance.className()
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
data class AnotherSingleClass(override val id: String, override val name: String = "Another") : ISingleInterface
data class OneMoreSingleClass(override val id: String, override val name: String = "OneMore") : ISingleInterface

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
```
See `com.mincor.kodiexample.KodiTest.kt` for full example. 

2) KodiReflect library 

It's use a kotlin reflect library to create instances and inject parameters into constructor. 
For start using this you need to implement `IKodi` interface to take all features of injection, like lazy property initialization or direct access to instances of given generic classe. 
It contains some functionality like: 
1) `single()` - create the one and only one instace of given generic class and save it in instanceMap
2) `instance()` - simple create an instance of given generic class
3) `instanceByTag()` - create an instance of given generic class and save it to instanceMap
4) `provider()` - it's create or give an holder class for your functions and save it into instanceMap to call later
5) `providerCall()` - call the providing function with tag, function and params
6) `providerCallByTag()` - call already saved provider by tag and params
7) `bind<Interface, Implementation>(... params)` - you can use binding function to map your interfaces to instance in just one line of code (Sinse version 0.1.5)
8) `constant(TAG, VALUE)` - map constants to tag (Sinse version 0.1.5)
9) `singleProvider(block: () -> T): T` - to initilize instance by yourself (since version 0.1.9)

You can initialize you instances, single, providers, binding and constants in just one place:
`val kodi = kodi { }`

in every function you can pass params for injection and put a tag to get it later.
You can use lazy instantiating by extension functions
- singleLazy
- providerLazy


```kotlin
data class UserData(val id:String, val name:String, val email:String)

data class Post(val id:String = "", val user:UserData? = null, val title:String = "", val desc:String = "")

interface IUserData {
        fun getData():String
    }
    
class UserDataInstance : IUserData {
        override fun getData():String {
            return "HELLO WORLD"
        }
    }

class MainActivity : AppCompatActivity(), IKodi {

 companion object {
        const val TAG_FUN_WITH_PARAMS = "fun_with_params"
        const val TAG_FUN_WITHOUT_PARAMS = "fun_without_params"
        const val TAG_FUN_FOR_INIT = "fun_for_init"

        private const val INSTANCE_TAG = "simple_tag"

        const val MY_GLOBAL_CONST = "global_const"
    }
    
    // now you can initialize Kodi and all the injected dependencies in one place
    val kodi = initKODI {
        single<UserData>(UUID.randomUUID().toString(), "Aleksandr", "sphc@yandex.ru")

        bind<IUserData, UserDataInstance>()

        constant(MY_GLOBAL_CONST, "https://myamazingaddress.com")

        provider(TAG_FUN_FOR_INIT, ::checkInstanceWithTag)
    }
    
   ///-------- LAZY VAL SECTION ----////
    private val singleInstance: UserData by singleLazy(UUID.randomUUID().toString(), "Aleksandr", "sphc@yandex.ru")
    private val providerWithReturnAndParams by providerLazy("", ::funcForProviderLazy, UUID.randomUUID().toString())
    private val userDataInstance:IUserData by singleLazy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("-----> SINGLE INSTANCE '${singleInstance.name}'")
        val posts = mutableListOf<Post>()
        (0..25).forEach {
            // Only instances with 'var' constructor params can be ovveriden by input params if another case check
            posts.add(instance(UUID.randomUUID().toString(), single<UserData>(listOf("1", "2", "3", "4", "5", "6")), "Title $it", "Desc $it"))
        }

        println("-----> INTERFACE BINDING TEST DATA ${userDataInstance.getData()}")
        println("-----> CONSTANTS TEST  ${constant<String>(MY_GLOBAL_CONST)}")

        val providerData = providerWithReturnAndParams.call()
        println("-----> PROVIDER LAZY DATA '${providerData.name}' and email = '${providerData.email}'")

       // val instanceWithTag = instanceByTag<Post>(INSTANCE_TAG, UUID.randomUUID().toString(), singleInstance, "Title with tag", "Desc with tag")
       // println("-----> INSTANCE WITH TAG TITLE '${instanceWithTag.title}'")

        // call the function without params
        providerCall(TAG_FUN_WITHOUT_PARAMS, ::funcWithoutParams)
        // call the function with params
        providerCall(TAG_FUN_WITH_PARAMS, ::funcWithParams, posts)

        /**
         * If you do not set the incoming function parameters it throw with RuntimeException
         */
        // check for singleInstance user
        val singleUser = single<UserData>()
        val providerWithReturns = providerCall("fun_with_return", ::funcWithReturnAndParams, singleUser)
        println("-----> RETURN FROM FUNC '$providerWithReturns'")

        /**
         * Call the function that we bind early in initKODI section
         */
        providerCall<Unit>(TAG_FUN_FOR_INIT)

        /**
         * Call already bounded function with new params
         */
        providerCallByTag<String>(TAG_FUN_WITH_PARAMS, single<UserData>())

        //call provider with RuntimeException, cause there is no providing function gives
        //providerCallByTag<String>("provider_call_without nothing", "HEHE")
        println("-----> END OF PRINTING")
    }
    
    private fun checkInstanceWithTag() {
        println("-----> HELLO instance with tag and desc = '${instanceByTag<Post>(INSTANCE_TAG).desc}'")
    }

    fun funcForProviderLazy(id:String):UserData {
        return instance(id, "Vasya", "vasiliy@gmail.ru")
    }

    fun funcWithoutParams() {
        println("-----> IM WITHOUT PARAMS")
    }

    fun funcWithParams(posts:Any) {
        println("-----> IM WITH PARAMS '$posts'")
    }

    fun funcWithReturnAndParams(user:UserData):String = user.id

    override fun onDestroy() {
        // clear all saved instances
        removeAll()
        super.onDestroy()
    }
}
```

Gradle:
```
build.gradle {
maven { url = "https://jitpack.io" }
}

// Standart Library
implementation 'com.github.Rasalexman.KODI:kodi:x.y.z'

// AndroidX Module
implementation 'com.github.Rasalexman.KODI:kodiandroidx:x.y.z'

// Annotation processing
kapt 'com.github.Rasalexman.KODI:kodigen:x.y.z'

// Old Reflection Library. It's a final version and i don't have any plans to support it in the future.
implementation 'com.github.Rasalexman.KODI:kodireflect:x.y.z'
```


License
----

MIT License

Copyright (c) 2021 Aleksandr Minkin (sphc@yandex.ru)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

