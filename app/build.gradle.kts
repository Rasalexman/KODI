import appdependencies.Builds.APP_ID
import appdependencies.Builds.BUILD_TOOLS
import appdependencies.Builds.COMPILE_VERSION
import appdependencies.Builds.MIN_VERSION
import appdependencies.Builds.TARGET_VERSION
import appdependencies.Libs
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
}

android {
    compileSdk = COMPILE_VERSION
    buildToolsVersion = BUILD_TOOLS
    defaultConfig {
        applicationId = APP_ID
        minSdk = MIN_VERSION
        targetSdk = TARGET_VERSION
        version = appdependencies.Builds.App.VERSION_NAME
        //versionCode = appdependencies.Builds.App.VERSION_CODE
        //versionName = appdependencies.Builds.App.VERSION_NAME
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true

        buildConfigField("String", "ApiKey", "\"026a257e7842ac9cac1fa627496b1468\"")
        buildConfigField("String", "IMAGES_URL", "\"https://image.tmdb.org/t/p/w500\"")
        buildConfigField("String", "SERVER_URL", "\"https://api.themoviedb.org/3/\"")
        buildConfigField("String", "IMAGES_BACKDROP_URL", "\"https://image.tmdb.org/t/p/original\"")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            //isDebuggable = true
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    /*sourceSets {
        getByName("main") {
            java.setSrcDirs(arrayListOf(
                    "${buildDir.absolutePath}/generated/source/kaptKotlin/",
                    "src/main/java"
            ))
            res.setSrcDirs(dirs)
        }
    }*/

    applicationVariants.forEach { variant ->
        variant.outputs.forEach { output ->
            val outputImpl = output as BaseVariantOutputImpl
            val project = project.name
            val sep = "_"
            val flavor = variant.flavorName
            val buildType = variant.buildType.name
            val version = variant.versionName

            val newApkName = "$project$sep$flavor$sep$buildType$sep$version.apk"
            outputImpl.outputFileName = newApkName
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        resources.excludes.add("META-INF/notice.txt")
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    kotlinOptions {
        languageVersion = "1.5"
        apiVersion = "1.5"
        jvmTarget = "11"
    }
}

kapt {
    useBuildCache = true
    generateStubs = false
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(project(":kodispatcher"))

    implementation(Libs.Core.coreKtx)
    implementation(Libs.Core.constraintlayout)
    implementation(Libs.Core.navigationFragmentKtx)
    implementation(Libs.Core.navigationUiKtx)
    implementation(Libs.Core.viewPager2)
    implementation(Libs.Core.paging)
    implementation(Libs.Core.swipeRefreshLayout)
    implementation(Libs.Core.material)

    implementation(Libs.Room.runtime)
    implementation(Libs.Room.ktx)

    implementation(Libs.Retrofit.core)
    implementation(Libs.Retrofit.moshi)
    implementation(Libs.Retrofit.logging)

    implementation(Libs.Lifecycle.livedataKtx)
    implementation(Libs.Lifecycle.viewmodelKtx)
    //implementation(appdependencies.Libs.Lifecycle.savedStateViewModel)
    //implementation(appdependencies.Libs.Lifecycle.extensions)
    //implementation(appdependencies.Libs.Lifecycle.common)

    implementation(Libs.FastAdapter.core)
    implementation(Libs.FastAdapter.ui)
    implementation(Libs.FastAdapter.uiExt)
    implementation(Libs.FastAdapter.diff)
    implementation(Libs.FastAdapter.paged)
    implementation(Libs.FastAdapter.scroll)

    implementation(Libs.KotPref.core)
    implementation(Libs.KotPref.liveData)

    implementation(Libs.ImageLoading.coil)

    implementation(Libs.Common.coroutinesmanager)
    implementation(Libs.Common.timber)
    implementation(Libs.Common.sticky)
    implementation(Libs.Common.leakCanary)


    testImplementation(Libs.Tests.junit)
    androidTestImplementation(Libs.Tests.runner)
    androidTestImplementation(Libs.Tests.espresso)

    //implementation("com.rasalexman.kodigen:kodigen:1.4.92")

    kapt(project(":kodigen"))
    //kapt("com.github.Rasalexman.KODI:kodigen:1.5.13")
    kapt(Libs.Room.kapt)
}
/*
configurations.all {
    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
        def requested = details.requested
        if (requested.group == "androidx") {
            if (!requested.name.startsWith("multidex")) {
                details.useVersion "${targetSdk}.+"
            }
        }
    }}*/

