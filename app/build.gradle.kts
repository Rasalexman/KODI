import appdependencies.Builds.APP_ID
import appdependencies.Builds.BUILD_TOOLS
import appdependencies.Builds.COMPILE_VERSION
import appdependencies.Builds.MIN_VERSION
import appdependencies.Builds.TARGET_VERSION
import appdependencies.Libs
import appdependencies.Versions
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
        versionCode = appdependencies.Builds.App.VERSION_CODE
        versionName = appdependencies.Builds.App.VERSION_NAME
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
            isDebuggable = true
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

    dexOptions {
        javaMaxHeapSize = "4g"
    }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        exclude("META-INF/notice.txt")
        //exclude("META-INF/gradle/incremental.annotation.processors")
    }

    // Declare the task that will monitor all configurations.
    configurations.all {
        // 2 Define the resolution strategy in case of conflicts.
        resolutionStrategy {
            // Fail eagerly on version conflict (includes transitive dependencies),
            // e.g., multiple different versions of the same dependency (group and name are equal).
            failOnVersionConflict()

            // Prefer modules that are part of this build (multi-project or composite build) over external modules.
            preferProjectModules()
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.5"
        apiVersion = "1.5"
    }
}

kapt {
    useBuildCache = true
    generateStubs = false
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", Versions.kotlin))

    implementation(project(":kodispatcher"))

    implementation(appdependencies.Libs.Core.coreKtx)
    implementation(appdependencies.Libs.Core.constraintlayout)
    implementation(appdependencies.Libs.Core.navigationFragmentKtx)
    implementation(appdependencies.Libs.Core.navigationUiKtx)
    implementation(appdependencies.Libs.Core.viewPager2)
    implementation(appdependencies.Libs.Core.paging)
    implementation(appdependencies.Libs.Core.swipeRefreshLayout)
    implementation(appdependencies.Libs.Core.material)

    implementation(appdependencies.Libs.Room.runtime)
    implementation(appdependencies.Libs.Room.ktx)

    implementation(appdependencies.Libs.Retrofit.core)
    implementation(appdependencies.Libs.Retrofit.moshi)
    implementation(appdependencies.Libs.Retrofit.logging)

    implementation(appdependencies.Libs.Lifecycle.livedataKtx)
    implementation(appdependencies.Libs.Lifecycle.viewmodelKtx)
    //implementation(appdependencies.Libs.Lifecycle.savedStateViewModel)
    //implementation(appdependencies.Libs.Lifecycle.extensions)
    //implementation(appdependencies.Libs.Lifecycle.common)

    implementation(appdependencies.Libs.FastAdapter.core)
    implementation(appdependencies.Libs.FastAdapter.ui)
    implementation(appdependencies.Libs.FastAdapter.uiExt)
    implementation(appdependencies.Libs.FastAdapter.diff)
    implementation(appdependencies.Libs.FastAdapter.paged)
    implementation(appdependencies.Libs.FastAdapter.scroll)

    implementation(appdependencies.Libs.KotPref.core)
    implementation(appdependencies.Libs.KotPref.liveData)

    implementation(appdependencies.Libs.ImageLoading.coil)

    implementation(appdependencies.Libs.Common.coroutinesmanager)
    //implementation(appdependencies.Libs.Common.circleimageview)
    implementation(appdependencies.Libs.Common.timber)
    implementation(appdependencies.Libs.Common.sticky)
    implementation(appdependencies.Libs.Common.leakCanary)


    testImplementation(Libs.Tests.junit)
    androidTestImplementation(Libs.Tests.runner)
    androidTestImplementation(Libs.Tests.espresso)

    //implementation("com.rasalexman.kodigen:kodigen:1.4.92")

    kapt(project(":kodigen"))
    //kapt("com.github.Rasalexman.KODI:kodigen:1.5.13")
    kapt(appdependencies.Libs.Room.kapt)
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

