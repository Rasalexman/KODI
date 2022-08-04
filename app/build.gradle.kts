import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    //kotlin("kapt")
}

android {

    val buildSdkVersion: Int by extra
    val minSdkVersion: Int by extra
    val appVersion: String by extra
    val appId: String by extra
    val codePath: String by rootProject.extra
    val srcDirs = listOf(codePath)

    compileSdk = buildSdkVersion
    defaultConfig {
        applicationId = appId
        minSdk = minSdkVersion
        targetSdk = buildSdkVersion
        version = appVersion
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

    sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
        }
    }

    kotlin {
        sourceSets.release {
            kotlin.srcDirs("build/generated/ksp/release/kotlin")
        }
        sourceSets.debug {
            kotlin.srcDirs("build/generated/ksp/debug/kotlin")
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/notice.txt")
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
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
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(project(":kodispatcher"))

    val settings = rootProject.extra
    val viewpager2: String by settings
    val paging: String by settings
    val coreKtx: String by settings
    val constraintlayout: String by settings
    val navigationFragment: String by settings
    val navigationUI: String by settings
    val material: String by settings
    val livedataKtx: String by settings
    val viewmodelKtx: String by settings
    val leakCanary: String by settings
    val swiperefreshlayout: String by settings
    val roomRuntime: String by settings
    val roomKtx: String by settings
    val roomKapt: String by settings
    val retrofit: String by settings
    val moshi: String by settings
    val logging: String by settings
    val coroutinesmanager: String by settings
    val timber: String by settings
    val sticky: String by settings
    val coil: String by settings
    val kotpref: String by settings
    val kotprefSupport: String by settings

    implementation(coreKtx)
    implementation(constraintlayout)
    implementation(navigationFragment)
    implementation(navigationUI)
    implementation(viewpager2)
    implementation(paging)
    implementation(swiperefreshlayout)
    implementation(material)

    implementation(roomRuntime)
    implementation(roomKtx)

    implementation(retrofit)
    implementation(moshi)
    implementation(logging)

    implementation(livedataKtx)
    implementation(viewmodelKtx)

    val fastadapterCore: String by settings
    val fastadapterUI: String by settings
    val fastadapterDiff: String by settings
    val fastadapterScroll: String by settings
    implementation(fastadapterCore)
    implementation(fastadapterUI)
    implementation(fastadapterDiff)
    implementation(fastadapterScroll)

    implementation(kotpref)
    implementation(kotprefSupport)

    implementation(coil)

    implementation(coroutinesmanager)
    implementation(timber)
    implementation(sticky)

    debugImplementation(leakCanary)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    ksp(project(":kodiksp"))
    //kapt(project(":kodigen"))
    ksp(roomKapt)
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

