package com.mincor.kodiexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mincor.kodi.core.*
import com.mincor.kodiexample.instances.Post
import com.mincor.kodiexample.single.UserData
import java.util.*

class MainActivity : AppCompatActivity(), IKodi {

    private val INSTANCE_TAG = "simple_tag"

    private val singleInstance: UserData by singleLazy(UUID.randomUUID().toString(), "Aleksandr", "sphc@yandex.ru")

    private val providerWithReturnAndParams by providerLazy("", ::funcForProviderLazy, UUID.randomUUID().toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("-----> SINGLE INSTANCE '${singleInstance.name}'")
        val posts = mutableListOf<Post>()
        (0..25).forEach {
            posts.add(instance(UUID.randomUUID().toString(), single<UserData>(), "Title $it", "Desc $it"))
        }

        val providerData = providerWithReturnAndParams.call()
        println("-----> PROVIDER LAZY DATA '${providerData.name}' and email = '${providerData.email}'")

       // val instanceWithTag = instanceByTag<Post>(INSTANCE_TAG, UUID.randomUUID().toString(), singleInstance, "Title with tag", "Desc with tag")
       // println("-----> INSTANCE WITH TAG TITLE '${instanceWithTag.title}'")

        val singleUser = single<UserData>()

        providerCall("", ::funcWithoutParams)

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

}
