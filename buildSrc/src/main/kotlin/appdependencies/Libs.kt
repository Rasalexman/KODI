package appdependencies

import appdependencies.Versions.appCoreX
import appdependencies.Versions.fastAdapterLib
import appdependencies.Versions.lifecycle
import appdependencies.Versions.retrofitLogging
import appdependencies.Versions.room

object Libs {
    object Core {
        //const val appcompat = "androidx.appcompat:appcompat:$appCompatX"
        const val coreKtx = "androidx.core:core-ktx:$appCoreX"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.Navigation.fragment}"
        const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.Navigation.ui}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.viewPager2}"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefresh}"
        const val paging = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
    }

    object Lifecycle {
        const val viewmodelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${lifecycle}"
        // kotlin live data extensions
        const val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${lifecycle}"
    }

    ///------ RECYCLER VIEW ADAPTER
    object FastAdapter {
        const val core = "com.mikepenz:fastadapter:$fastAdapterLib@aar"
        const val ui = "com.mikepenz:fastadapter-extensions-ui:${fastAdapterLib}"
        const val diff = "com.mikepenz:fastadapter-extensions-diff:${fastAdapterLib}"
        const val paged = "com.mikepenz:fastadapter-extensions-paged:${fastAdapterLib}"
        const val uiExt = "com.mikepenz:fastadapter-extensions-ui:${fastAdapterLib}"
        const val scroll = "com.mikepenz:fastadapter-extensions-scroll:${fastAdapterLib}" // scroll helpers
    }

    //--- ROOM DB
    object Room {
        const val runtime = "androidx.room:room-runtime:$room"
        const val ktx = "androidx.room:room-ktx:$room"
        const val kapt = "androidx.room:room-compiler:$room"
    }

    //------ HTTP
    object Retrofit {
        const val core = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val moshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
        const val logging = "com.github.ihsanbal:LoggingInterceptor:${retrofitLogging}"
    }

    ///------ VIEWS
    object ImageLoading {
        const val coil = "io.coil-kt:coil:${Versions.coil}"
    }

    object KotPref {
        //--- SHARED PREFERENCE
        const val core = "com.chibatching.kotpref:kotpref:${Versions.kotPref}"
        const val liveData = "com.chibatching.kotpref:livedata-support:${Versions.kotPref}"
    }

    object Common {

        // COROUTINES MANAGER
        const val coroutinesmanager = "com.github.Rasalexman:coroutinesmanager:${Versions.coroutinesManager}"

        //---- LOGGING TIMER =)
        const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

        const val sticky = "com.github.Rasalexman:Sticky:${Versions.sticky}"

        //--- LEAK DETECTOR
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"
    }

    object Processor {
       const val kotlinpoet = "com.squareup:kotlinpoet:1.10.2"
       const val autoService = "com.google.auto.service:auto-service:1.0.1"
    }

    object Tests {
        const val junit = "junit:junit:${Versions.junit}"
        const val runner = "com.android.support.test:runner:${Versions.runner}"
        const val espresso = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
    }

}