import appdependencies.Apps.APP_ID
import appdependencies.Apps.BUILD_TOOLS
import appdependencies.Apps.COMPILE_VERSION
import appdependencies.Apps.MIN_VERSION
import appdependencies.Apps.TARGET_VERSION
import appdependencies.Apps.VERSION_CODE
import appdependencies.Apps.VERSION_NAME
import appdependencies.Libs
import appdependencies.Versions
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(COMPILE_VERSION)
    buildToolsVersion = BUILD_TOOLS
    defaultConfig {
        applicationId = APP_ID
        minSdkVersion(MIN_VERSION)
        targetSdkVersion(TARGET_VERSION)
        versionCode = VERSION_CODE
        versionName = VERSION_NAME
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

        buildConfigField ("String", "ApiKey", "\"026a257e7842ac9cac1fa627496b1468\"")
        buildConfigField ("String", "IMAGES_URL", "\"https://image.tmdb.org/t/p/w500\"")
        buildConfigField ("String", "SERVER_URL", "\"https://api.themoviedb.org/3/\"")
        buildConfigField ("String", "IMAGES_BACKDROP_URL", "\"https://image.tmdb.org/t/p/original\"")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
/*
android {
    compileSdkVersion COMPILE_VERSION
    buildToolsVersion = BUILD_TOOLS_VERSION
    defaultConfig {
        applicationId "com.mincor.kodiexample"
        minSdkVersion MIN_VERSION
        targetSdkVersion TARGET_VERSION
        versionCode 10002
        versionName "1.0.2"
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

    testImplementation 'junit:junit:4.13-rc-1'
    androidTestImplementation 'androidx.test:runner:1.3.0-alpha02'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0-alpha02'

    implementation project(":kodi")
    implementation project(":kodiandroidx")
    //implementation project(":kodireflect")

    kapt "androidx.room:room-compiler:$roomVersion"
}*/
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
