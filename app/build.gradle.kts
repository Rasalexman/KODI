import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    kotlin("android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp") version libs.versions.ksp
}

android {

    val appId: String by extra
    val appVersion: String by extra
    compileSdk = 36
    defaultConfig {
        namespace = "com.mincor.kodiexample"
        applicationId = appId
        minSdk = 23
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

    lint {
        baseline = file("lint-baseline.xml")
        disable.add("RestrictedApi")
    }

    publishing {
        singleVariant("debug") {}
        singleVariant("release") {}
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

//    sourceSets {
//        getByName("main") {
//            java.setSrcDirs(srcDirs)
//        }
//    }

    kotlin {
        sourceSets.release {
            kotlin.srcDirs("build/generated/ksp/release/kotlin")
        }
        sourceSets.debug {
            kotlin.srcDirs("build/generated/ksp/debug/kotlin")
        }
    }

    packaging {
        resources.excludes.add("META-INF/notice.txt")
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
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

    implementation(libs.coreKtx)
    implementation(libs.constraintlayout)
    implementation(libs.navigationFragment)
    implementation(libs.navigationUI)
    implementation(libs.viewpager)
    implementation(libs.paging)
    implementation(libs.swiperefreshlayout)
    implementation(libs.material)

    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)

    implementation(libs.retrofit)
    implementation(libs.moshi)
    implementation(libs.logging)

    implementation(libs.viewmodelKtx)

    implementation(libs.fastadapterCore)
    implementation(libs.fastadapterUI)
    implementation(libs.fastadapterDiff)
    implementation(libs.fastadapterScroll)

    implementation(libs.kotpref)

    implementation(libs.coil)

    implementation(libs.coroutinesmanager)
    implementation(libs.timber)
    implementation(libs.sticky)

    debugImplementation(libs.leakCanary)
    testImplementation(libs.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso)

    ksp(project(":kodiksp"))
    ksp(libs.roomCompiler)
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

