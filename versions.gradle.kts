//------ CONFIG DATA
extra["appId"] = "com.mincor.kodiexample"
extra["kodiKmpNamespace"] = "com.rasalexman.kodi"
extra["jitpackPath"] = "https://jitpack.io"
extra["pluginsPath"] = "https://plugins.gradle.org/m2/"
extra["codePath"] = "src/main/kotlin"
extra["codePathKmp"] = "src/commonMain/kotlin"
extra["resPath"] = "src/main/res"

//------- LIBS VERSIONS
val navigation = "2.8.3"
val leakcanary = "2.14"
val coroutines = "1.9.0"
val core: String = "1.13.1"
val constraintLayout = "2.2.0-rc01"
val material = "1.12.0"
val viewPager2 = "1.1.0"
val recyclerView = "1.4.0-rc01"
val lifecycle = "2.8.6"
val fragment: String = "1.8.4"
val paging: String = "3.3.2"
val swiperefreshlayout: String = "1.1.0"
val room: String = "2.6.1"
val retrofit = "2.9.0"
val retrofitLogging = "3.0.0"
val coroutinesManager = "1.4.4"
val timber = "5.0.1"
val sticky = "1.1.5"
val coil = "2.3.0"
val kotPref = "2.13.2"
val fastAdapterLib = "5.7.0"
val ksp = "2.0.21-1.0.25"
val kotlinpoet = "1.18.1"
val autoService = "1.1.1"

//----- extra navigation
extra["navigation"] = navigation

//------- Libs path
extra["leakCanary"] = "com.squareup.leakcanary:leakcanary-android:$leakcanary"
extra["coroutines"] = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines"
extra["coreKtx"] = "androidx.core:core-ktx:$core"
extra["constraintlayout"] = "androidx.constraintlayout:constraintlayout:$constraintLayout"
extra["navigationUI"] = "androidx.navigation:navigation-ui-ktx:$navigation"
extra["navigationFragment"] = "androidx.navigation:navigation-fragment-ktx:$navigation"
extra["material"] = "com.google.android.material:material:$material"
extra["viewpager2"] = "androidx.viewpager2:viewpager2:$viewPager2"
extra["swiperefreshlayout"] = "androidx.swiperefreshlayout:swiperefreshlayout:$swiperefreshlayout"
extra["recyclerview"] = "androidx.recyclerview:recyclerview:$recyclerView"
extra["fragmentKtx"] = "androidx.fragment:fragment-ktx:$fragment"
extra["paging"] = "androidx.paging:paging-runtime:$paging"
extra["viewmodelKtx"] = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle"
extra["livedataKtx"] = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle"
extra["roomRuntime"] = "androidx.room:room-runtime:$room"
extra["roomKtx"] = "androidx.room:room-ktx:$room"
extra["roomKapt"] = "androidx.room:room-compiler:$room"
extra["retrofit"] = "com.squareup.retrofit2:retrofit:${retrofit}"
extra["moshi"] = "com.squareup.retrofit2:converter-moshi:${retrofit}"
extra["logging"] = "com.github.ihsanbal:LoggingInterceptor:${retrofitLogging}"
extra["coroutinesmanager"] = "com.github.Rasalexman:coroutinesmanager:${coroutinesManager}"
extra["timber"] = "com.jakewharton.timber:timber:${timber}"
extra["sticky"] = "com.github.Rasalexman:Sticky:${sticky}"
extra["coil"] = "io.coil-kt:coil:${coil}"
extra["kotpref"] = "com.chibatching.kotpref:kotpref:${kotPref}"
extra["kotprefSupport"] = "com.chibatching.kotpref:livedata-support:${kotPref}"
extra["kotlinpoet"] = "com.squareup:kotlinpoet:$kotlinpoet"
extra["kotlinpoetKsp"] = "com.squareup:kotlinpoet-ksp:$kotlinpoet"
extra["autoService"] = "com.google.auto.service:auto-service:$autoService"
extra["kspapi"] = "com.google.devtools.ksp:symbol-processing-api:$ksp"

extra["fastadapterCore"] = "com.mikepenz:fastadapter:${fastAdapterLib}@aar"
extra["fastadapterUI"] = "com.mikepenz:fastadapter-extensions-ui:${fastAdapterLib}"
extra["fastadapterDiff"] = "com.mikepenz:fastadapter-extensions-diff:${fastAdapterLib}"
extra["fastadapterPaged"] = "com.mikepenz:fastadapter-extensions-paged:${fastAdapterLib}"
extra["fastadapterScroll"] = "com.mikepenz:fastadapter-extensions-scroll:${fastAdapterLib}"
