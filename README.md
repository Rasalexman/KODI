# KODI
KOtlin Dependency Injection (KODI) 

[ ![Kotlin 1.2.71](https://img.shields.io/badge/Kotlin-1.2.71-blue.svg)](http://kotlinlang.org) [ ![Download](https://api.bintray.com/packages/sphc/FlairFramework/flair-framework/images/download.svg) ](https://bintray.com/sphc/FlairFramework/flair-framework/_latestVersion)

This is simple and useful dependency injection tool for used in your regular projects. It's use a kotlin reflect library to create instances and inject parameters into constructor. 

For start using this you need to implement `IKodi` interface to take all features of injection, like lazy property initialization or direct access to instances of given generic classe. 
It contains three type of functionality: 
1) single() - create a one and only one instace of given generic class and save it in instanceMap
2) instance() - simple create an instance of given generic class
3) instanceByTag() - create an instance of given generic class and save it to instanceMap
4) provider() - it's create or give an holder class for your functions and save it into instanceMap to call later
5) providerCall() - call the providing function with tag, function and params
6) providerCallByTag() - call already saved provider by tag and params

in every function you can pass params for injection and put a tag to get it later.
You can use lazy instantiating by extension functions
- singleLazy
- providerLazy

```kotlin
data class UserData(val id:String, val name:String, val email:String)

data class Post(val id:String = "", val user:UserData? = null, val title:String = "", val desc:String = "")

class MainActivity : AppCompatActivity(), IKodi {

    private val INSTANCE_TAG = "simple_tag"
    
    // LAZY instatiating a singleton object with given constructor params
    private val singleInstance: UserData by singleLazy(UUID.randomUUID().toString(), "Aleksandr", "sphc@yandex.ru")

    // Lazy Provider function for call later with given params
    private val providerWithReturnAndParams by providerLazy("", ::funcForProviderLazy, UUID.randomUUID().toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("-----> SINGLE INSTANCE '${singleInstance.name}'")
        val posts = mutableListOf<Post>()
        (0..25).forEach {
            // Only instances with 'var' constructor params can be overriden by input params
            posts.add(instance(UUID.randomUUID().toString(), single<UserData>(listOf("1", "2", "3", "4", "5", "6")), "Title $it", "Desc $it"))
        }

        val providerData = providerWithReturnAndParams.call()
        println("-----> PROVIDER LAZY DATA '${providerData.name}' and email = '${providerData.email}'")

       // val instanceWithTag = instanceByTag<Post>(INSTANCE_TAG, UUID.randomUUID().toString(), singleInstance, "Title with tag", "Desc with tag")
       // println("-----> INSTANCE WITH TAG TITLE '${instanceWithTag.title}'")

        // take an singleton of given class
        val singleUser = single<UserData>()
        // immediatly call the function
        providerCall("", ::funcWithoutParams)
        // this is a tag for provider
        val tagForParams = "fun_withParams"
        providerCall(tagForParams, ::funcWithParams, posts)

        /**
         * If you do not set the incoming function parameters it throw with RuntimeException
         */
        val providerWithReturns = providerCall("fun_with_return", ::funcWithReturnAndParams, singleUser)
        println("-----> RETURN FROM FUNC '$providerWithReturns'")

        checkInstanceWithTag()

        /**
         * Call already bounded function with new params
         */
        providerCallByTag<String>(tagForParams, single<UserData>())

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

