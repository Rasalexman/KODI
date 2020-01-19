import appdependencies.Builds.APP_ID
import appdependencies.Builds.BUILD_TOOLS
import appdependencies.Builds.COMPILE_VERSION
import appdependencies.Builds.MIN_VERSION
import appdependencies.Builds.TARGET_VERSION
import appdependencies.Libs
import appdependencies.Versions
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import resources.Resources.App.dirs
import resources.Resources.App.javaDirs

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdkVersion(COMPILE_VERSION)
    buildToolsVersion = BUILD_TOOLS
    defaultConfig {
        applicationId = APP_ID
        minSdkVersion(MIN_VERSION)
        targetSdkVersion(TARGET_VERSION)
        versionCode = appdependencies.Builds.App.VERSION_CODE
        versionName = appdependencies.Builds.App.VERSION_NAME
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        javaCompileOptions.annotationProcessorOptions {
            includeCompileClasspath = true
        }

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

    sourceSets {
        getByName("main") {
            java.setSrcDirs(javaDirs)
            res.setSrcDirs(dirs)
        }
    }

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

    tasks.withType<KotlinCompile>().all {
        kotlinOptions.suppressWarnings = true
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.noReflect = true
        kotlinOptions.freeCompilerArgs += listOf(
                "-XXLanguage:+InlineClasses"
        )
    }

    packagingOptions {
        exclude("META-INF/notice.txt")
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

    androidExtensions {
        isExperimental = true
        defaultCacheImplementation = org.jetbrains.kotlin.gradle.internal.CacheImplementation.HASH_MAP
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", Versions.kotlin))

    implementation(project(":kodi"))
    implementation(project(":kodiandroidx"))

    implementation(appdependencies.Libs.Core.appcompat)
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
    implementation(appdependencies.Libs.Lifecycle.savedStateViewModel)
    implementation(appdependencies.Libs.Lifecycle.extensions)
    implementation(appdependencies.Libs.Lifecycle.common)

    implementation(appdependencies.Libs.FastAdapter.core)
    implementation(appdependencies.Libs.FastAdapter.ui)
    implementation(appdependencies.Libs.FastAdapter.uiExt)
    implementation(appdependencies.Libs.FastAdapter.diff)
    implementation(appdependencies.Libs.FastAdapter.paged)

    implementation(appdependencies.Libs.KotPref.core)
    implementation(appdependencies.Libs.KotPref.liveData)

    implementation(appdependencies.Libs.ImageLoading.coil)

    implementation(appdependencies.Libs.Common.coroutinesmanager)
    implementation(appdependencies.Libs.Common.circleimageview)
    implementation(appdependencies.Libs.Common.timber)
    implementation(appdependencies.Libs.Common.sticky)


    testImplementation(Libs.Tests.junit)
    androidTestImplementation(Libs.Tests.runner)
    androidTestImplementation(Libs.Tests.espresso)

    kapt(appdependencies.Libs.Room.kapt)
}



/*
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'

android {
    compileSdkVersion COMPILE_VERSION
    buildToolsVersion = BUILD_TOOLS_VERSION
    defaultConfig {
        applicationId "com.mincor.kodiexample"
        minSdkVersion MIN_VERSION
        targetSdkVersion TARGET_VERSION
        versionCode 10003
        versionName "1.0.3"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled false

        buildConfigField "String", "ApiKey", "\"026a257e7842ac9cac1fa627496b1468\""
        buildConfigField "String", "IMAGES_URL", "\"https://image.tmdb.org/t/p/w500\""
        buildConfigField "String", "SERVER_URL", "\"https://api.themoviedb.org/3/\""
        buildConfigField "String", "IMAGES_BACKDROP_URL", "\"https://image.tmdb.org/t/p/original\""
    }
    buildTypes {
        debug {
            minifyEnabled false
            debuggable true
        }

        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility = 1.8
        targetCompatibility = 1.8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = ["-XXLanguage:+InlineClasses", "-Xexperimental=kotlin.ExperimentalUnsignedTypes"]
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation "androidx.appcompat:appcompat:$appCompatXVersion"
    implementation "androidx.core:core-ktx:$appCoreXVersion"
    implementation "androidx.constraintlayout:constraintlayout:$constraintlayout"
    implementation "androidx.navigation:navigation-fragment-ktx:$navigation.fragment"
    implementation "androidx.navigation:navigation-ui-ktx:$navigation.ui"

    ///------ RECYCLER VIEW ADAPTER
    implementation("com.mikepenz:fastadapter:$fastAdapterLib@aar") { transitive = true }
    implementation "com.mikepenz:fastadapter-extensions-ui:${fastAdapterLib}"
    implementation "com.mikepenz:fastadapter-extensions-diff:${fastAdapterLib}"

    //--- ROOM DB
    implementation "androidx.room:room-runtime:$roomVersion"
    implementation "androidx.room:room-ktx:$roomVersion"

    //------ HTTP
    implementation "com.squareup.retrofit2:retrofit:$retrofitVersion"
    implementation "com.squareup.retrofit2:converter-moshi:$retrofitVersion"
    implementation("com.github.ihsanbal:LoggingInterceptor:${retrofitLogginVersion}") {
        exclude group: 'org.json', module: 'json'
    }

    ///------ VIEWS
    implementation "io.coil-kt:coil:$coilVersion"

    // COROUTINES MANAGER
    implementation "com.rasalexman.coroutinesmanager:coroutinesmanager:$coroutinesManagerVersion"

    // Sticky
    implementation "com.rasalexman.sticky:sticky:$stickyVersion"

    testImplementation 'junit:junit:4.13'
    androidTestImplementation 'androidx.test:runner:1.3.0-alpha03'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0-alpha03'

    implementation project(":kodi")
    implementation project(":kodiandroidx")
    //implementation project(":kodireflect")

    kapt "androidx.room:room-compiler:$roomVersion"
}
*/
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

