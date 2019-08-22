# KODI
KOtlin Dependency Injection (KODI) 

[ ![Kotlin 1.3.41](https://img.shields.io/badge/Kotlin-1.3.41-blue.svg)](http://kotlinlang.org) [ ![Download](https://api.bintray.com/packages/sphc/Kodi/kodi/images/download.svg) ](https://bintray.com/sphc/Kodi/kodi/_latestVersion)

This is simple and useful dependency injection tool for used in your regular projects. It's use a kotlin reflect library to create instances and inject parameters into constructor. 

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

And from 0.1.5 you can initialize you instances, single, providers, binding and constants in just one place:
`val kodi = initKODI { }`

in every function you can pass params for injection and put a tag to get it later.
You can use lazy instantiating by extension functions
- singleLazy
- providerLazy

From version 0.1.7 you can use: 
- instanceLazy
- instanceLazyByTag

From version 0.1.9 you can use mutable lazy initialization:
- singleMutableLazy
- instanceMutableLazy
- providerMutableLazy


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
implementation 'com.rasalexman.kodi:kodi:x.y.z'
```

Maven:
```
<dependency>
  <groupId>com.rasalexman.kodi</groupId>
  <artifactId>kodi</artifactId>
  <version>x.y.z</version>
  <type>pom</type>
</dependency>
```


License
----

MIT License

Copyright (c) 2018 Aleksandr Minkin (sphc@yandex.ru)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

